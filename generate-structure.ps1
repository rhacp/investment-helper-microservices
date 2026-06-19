Get-ChildItem -Recurse -Include *.java,*.tsx,*.ts,*.jsx,*.js,*.css,*.html,*.yml,*.yaml |
Where-Object {
    $_.FullName -notmatch '\\(node_modules|target|build|\.git|\.idea|\.m2|dist)\\'
} |
ForEach-Object {
    $_.FullName.Replace((Get-Location).Path + "\", "")
} |
Out-File project-structure.txt