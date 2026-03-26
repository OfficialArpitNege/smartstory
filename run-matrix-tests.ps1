$ErrorActionPreference='Stop'
$base='http://localhost:8080'
$groupId = 1

try {
  Invoke-RestMethod -Method Post -Uri "$base/api/groups/$groupId/members" -ContentType 'application/json' -Body (@{ userId = 2 } | ConvertTo-Json) | Out-Null
} catch {}

$cases = @(
@{No=1; Content='Hello Friends'; Mode='SHOW'; Groups=@($groupId); Exceptions=@(); Viewer=1; Expected=$true; Notes='Creator always sees'},
@{No=2; Content='Hello Friends'; Mode='SHOW'; Groups=@($groupId); Exceptions=@(); Viewer=2; Expected=$true; Notes='In group can view'},
@{No=3; Content='Hello Friends'; Mode='SHOW'; Groups=@($groupId); Exceptions=@(); Viewer=3; Expected=$false; Notes='Not in group cannot view'},
@{No=4; Content='Secret Message'; Mode='HIDE'; Groups=@($groupId); Exceptions=@(); Viewer=1; Expected=$true; Notes='Creator always sees'},
@{No=5; Content='Secret Message'; Mode='HIDE'; Groups=@($groupId); Exceptions=@(); Viewer=2; Expected=$false; Notes='In hidden group cannot view'},
@{No=6; Content='Secret Message'; Mode='HIDE'; Groups=@($groupId); Exceptions=@(); Viewer=3; Expected=$true; Notes='Not in hidden group can view'},
@{No=7; Content='VIP Only'; Mode='SHOW'; Groups=@($groupId); Exceptions=@(3); Viewer=3; Expected=$false; Notes='SHOW + exception expected block'},
@{No=8; Content='VIP Only'; Mode='SHOW'; Groups=@($groupId); Exceptions=@(3); Viewer=2; Expected=$true; Notes='In group not excepted'},
@{No=9; Content='Private Hide'; Mode='HIDE'; Groups=@($groupId); Exceptions=@(2); Viewer=2; Expected=$false; Notes='HIDE + exception expected block'},
@{No=10; Content='Private Hide'; Mode='HIDE'; Groups=@($groupId); Exceptions=@(2); Viewer=3; Expected=$true; Notes='Not in group can view'},
@{No=11; Content='Public Show'; Mode='SHOW'; Groups=@(); Exceptions=@(); Viewer=2; Expected=$true; Notes='No groups should be public'}
)

$results=@()
foreach($c in $cases){
  $body = @{ userId = 1; content = $c.Content; mode = $c.Mode; groupIds = $c.Groups; exceptionUserIds = $c.Exceptions } | ConvertTo-Json -Depth 5
  $story = Invoke-RestMethod -Method Post -Uri "$base/api/stories" -ContentType 'application/json' -Body $body
  $sid = [int64]$story.id
  $vis = Invoke-RestMethod -Method Get -Uri "$base/api/stories/$sid/visibility?userId=$($c.Viewer)"
  $actual = [bool]$vis.canView
  $results += [pscustomobject]@{ Case=$c.No; StoryId=$sid; Expected=$c.Expected; Actual=$actual; Pass=($actual -eq $c.Expected); Notes=$c.Notes }
}

$results | ConvertTo-Json -Depth 4 | Set-Content -Path "d:\arpit coding\smart_story\test-matrix-results.json"
$results | Format-Table -AutoSize | Out-String | Write-Output
