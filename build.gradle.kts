plugins { base }
tasks.named("build") { dependsOn(":fabric-26.3:build") }
tasks.named("clean") { dependsOn(":fabric-26.3:clean") }
