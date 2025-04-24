import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'extractDomain'
})
export class ExtractDomainPipe implements PipeTransform {
  transform(url: string): string {
    const domain = url.replace(/(^\w+:|^)\/\//, '');
    return domain;
  }
}
