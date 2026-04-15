enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "aicore"

include(
    ":core:common-interfaces",
    ":core:network",
    ":core:engine-adapter",
    ":core:session-manager",
    ":feature:model-hub",
    ":feature:input-core",
    ":feature:settings",
    ":feature:chat"
)
