rootProject.name = "spot"

include("common")
include("core")
include("study")
include("worker")

// core 하위 모듈
include("core:boot")
include("core:member")
include("core:notification")
include("core:post")
include("core:point")

// study 하위 모듈
include("study:boot")
include("study:core")
include("study:schedule")
include("study:review")
include("study:todo")

// worker 하위 모듈
include("worker:boot")
