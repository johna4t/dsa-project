param (
    [string]$ClassName = $(Read-Host "Enter class name (e.g. my-dummy)")
)

$BasePath = "src/app/$ClassName"

if (Test-Path $BasePath) {
    $response = Read-Host "Folder '$BasePath' already exists. Overwrite? (y/n)"
    if ($response -ne 'y') {
        Write-Host "Aborted."
        exit
    }
    Remove-Item $BasePath -Recurse -Force
}

# Generate Angular components
ng generate component "$ClassName/create-$ClassName"
ng generate component "$ClassName/update-$ClassName"
ng generate component "$ClassName/$ClassName-details"
ng generate component "$ClassName/$ClassName-list"

# Generate service
ng generate service "$ClassName/$ClassName"

# Generate PascalCase class name
$Parts = $ClassName -split '-'
$PascalName = ($Parts | ForEach-Object { $_.Substring(0,1).ToUpper() + $_.Substring(1) }) -join ''

# Write empty data class
$DataClassPath = "src/app/$ClassName/$ClassName.ts"
"export class $PascalName { }" | Set-Content $DataClassPath

Write-Host "`n✅ Components, service, and data class generated under src/app/$ClassName"
