import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';

import { DataProcessingActivityService } from '../data-processing-activity.service';
import { DataProcessorService } from '../../data-processor/data-processor.service';
import { DataContentDefinitionService } from '../../data-content-definition/data-content-definition.service';

import { DataProcessor } from '../../data-processor/data-processor';
import { DataContentDefinition } from '../../data-content-definition/data-content-definition';
import { DataProcessingActivity } from '../data-processing-activity';
import { NavigationService } from '../../access/navigation.service';

type SourcePage = 'data-processor' | 'data-content-definition' | 'other' | null;

@Component({
  selector: 'app-create-data-processing-activity',
  templateUrl: './create-data-processing-activity.component.html',
  styleUrls: ['./create-data-processing-activity.component.css'],
})
export class CreateDataProcessingActivityComponent implements OnInit {
  form!: FormGroup;

  // Launch context (via query params OR inferred from NavigationService returnTo)
  sourcePage: SourcePage = null;
  qpDataProcessorId?: number | null;
  qpDataContentDefinitionId?: number | null;

  dataProcessors: DataProcessor[] = [];
  dataContentDefinitions: DataContentDefinition[] = [];

  submitting = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,                       // <-- add Router to read navigation state
    private activityService: DataProcessingActivityService,
    private dataProcessorService: DataProcessorService,
    private dataContentDefinitionService: DataContentDefinitionService,
    private navigation: NavigationService,
  ) {}

  ngOnInit(): void {
    // 1) Prefer explicit query params if provided
    this.sourcePage = (this.route.snapshot.queryParamMap.get('from') as SourcePage) ?? null;
    const dpIdParam = this.route.snapshot.queryParamMap.get('dataProcessorId');
    const dcdIdParam = this.route.snapshot.queryParamMap.get('dataContentDefinitionId');
    this.qpDataProcessorId = dpIdParam ? Number(dpIdParam) : null;
    this.qpDataContentDefinitionId = dcdIdParam ? Number(dcdIdParam) : null;

    // 2) If not provided, infer from NavigationService's returnTo (stored in router state)
    if (!this.sourcePage && !this.qpDataProcessorId && !this.qpDataContentDefinitionId) {
      // read from current navigation first (best place to get extras.state)
      const nav = this.router.getCurrentNavigation();
      const returnTo =
        (nav?.extras?.state as { returnTo?: string } | undefined)?.returnTo ??
        (window.history.state && (window.history.state as any).returnTo); // fallback

      if (returnTo) {
        const inferred = this.inferLaunchContextFromUrl(returnTo);
        this.sourcePage = inferred.source;
        this.qpDataProcessorId = inferred.dataProcessorId ?? null;
        this.qpDataContentDefinitionId = inferred.dataContentDefinitionId ?? null;
      }
    }

    // Build form
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      dataProcessorId: [null, Validators.required],
      dataContentDefinitionId: [null, Validators.required],
    });

    // Load dropdowns, then apply defaults based on context
    forkJoin({
      processors: this.dataProcessorService.getDataProcessorList(),
      dcds: this.dataContentDefinitionService.getDataContentDefinitionList(),
    }).subscribe({
      next: ({ processors, dcds }) => {
        this.dataProcessors = processors ?? [];
        this.dataContentDefinitions = dcds ?? [];
        this.applyLaunchDefaults();
      },
      error: (err) => console.error('Error loading dropdown options:', err),
    });
  }

  /**
   * Recognize caller URLs like:
   *  - /update-data-processor/123 or /view-data-processor/123
   *  - /update-data-content-definition/456 or /view-data-content-definition/456
   */
  private inferLaunchContextFromUrl(url: string): {
    source: SourcePage;
    dataProcessorId?: number;
    dataContentDefinitionId?: number;
  } {
    const dp = url.match(/\/(?:update|view)-data-processor\/(\d+)/i);
    if (dp?.[1]) return { source: 'data-processor', dataProcessorId: Number(dp[1]) };

    const dcd = url.match(/\/(?:update|view)-data-content-definition\/(\d+)/i);
    if (dcd?.[1]) return { source: 'data-content-definition', dataContentDefinitionId: Number(dcd[1]) };

    return { source: 'other' };
  }

  /**
   * Apply required defaults:
   * - From Data Processor → DP = caller DP, DCD = first
   * - From DCD           → DCD = caller DCD, DP = first
   * - From other         → DP = first, DCD = first
   * If provided ids don't exist, fallback to first in list.
   */
  private applyLaunchDefaults(): void {
    const firstDP = this.dataProcessors[0]?.id ?? null;
    const firstDCD = this.dataContentDefinitions[0]?.id ?? null;

    const resolveFromList = (
      id: number | null | undefined,
      list: { id: number }[],
      fallback: number | null,
    ) => (id && list.some((x) => Number(x.id) === Number(id)) ? Number(id) : (fallback ?? null));

    if (this.sourcePage === 'data-processor') {
      const dpId = resolveFromList(this.qpDataProcessorId, this.dataProcessors, firstDP);
      this.form.patchValue({ dataProcessorId: dpId, dataContentDefinitionId: firstDCD });
    } else if (this.sourcePage === 'data-content-definition') {
      const dcdId = resolveFromList(this.qpDataContentDefinitionId, this.dataContentDefinitions, firstDCD);
      this.form.patchValue({ dataProcessorId: firstDP, dataContentDefinitionId: dcdId });
    } else {
      this.form.patchValue({ dataProcessorId: firstDP, dataContentDefinitionId: firstDCD });
    }
  }

  get nameCtrl() {
    return this.form.controls['name'];
  }

  get isSubmitDisabled(): boolean {
    return this.submitting || this.form.invalid;
  }

  private getSelectedDataProcessor(): DataProcessor | undefined {
    const dpId: number = this.form.value.dataProcessorId;
    return this.dataProcessors.find((p) => Number(p.id) === Number(dpId));
  }

  /** Include controller.id if your backend needs tenant validation */
  private buildCreatePayload(): DataProcessingActivity | null {
    const selectedDP = this.getSelectedDataProcessor();
    const controllerId = selectedDP?.controller?.id;

    if (!controllerId) {
      console.error('Cannot create activity: selected Data Processor has no controller.id');
      return null;
    }

    return {
      id: 0,
      name: this.form.value.name,
      description: this.form.value.description,
      dataProcessor: { id: this.form.value.dataProcessorId, controller: { id: controllerId } as any } as any,
      dataContentDefinition: { id: this.form.value.dataContentDefinitionId } as any,
      actionsPerformed: [], // create without actions; add later via update
    };
  }

onSubmit(): void {
  if (this.isSubmitDisabled) return;
  const payload = this.buildCreatePayload();
  if (!payload) return;

  this.submitting = true;
  this.activityService.postDataProcessingActivity(payload).subscribe({
    next: () => {
      this.submitting = false;
      this.returnToUpdatePageWithFlagOrFallback(); // << generalized
    },
    error: (err) => {
      this.submitting = false;
      console.error('Error creating data processing activity:', err);
    },
  });
}

  /** Read the caller URL NavigationService stored in router state */
private getReturnToUrl(): string | null {
  const nav = this.router.getCurrentNavigation();
  const fromNav = (nav?.extras?.state as { returnTo?: string } | undefined)?.returnTo ?? null;
  const fromHistory = (window.history.state && (window.history.state as any).returnTo) || null;
  return fromNav || fromHistory || null;
}

/** If returnTo is /update-data-processor/:id, go there and tag the navigation. Else fallback. */
// Replace the old "returnToUpdateDataProcessorWithFlagOrFallback" with:
private returnToUpdatePageWithFlagOrFallback(): void {
  const returnTo = this.getReturnToUrl();
  const matchDp  = returnTo?.match(/\/update-data-processor\/(\d+)$/i);
  const matchDcd = returnTo?.match(/\/update-data-content-definition\/(\d+)$/i);

  if (matchDp?.[1]) {
    this.router.navigateByUrl(`/update-data-processor/${matchDp[1]}`, {
      state: { cameFromCreateDpa: true },
    });
    return;
  }

  if (matchDcd?.[1]) {
    this.router.navigateByUrl(`/update-data-content-definition/${matchDcd[1]}`, {
      state: { cameFromCreateDpa: true },
    });
    return;
  }

  // Fallback if nothing matched
  this.navigation.goBackOr(['/']);
}


/** Use this for the Cancel button */
cancel(): void {
  this.returnToUpdatePageWithFlagOrFallback(); // << generalized
}
  goBack(): void {
    this.navigation.goBackOr(['/']); // uses returnTo or history stack, else '/'
  }
}
