# :framework:ui

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :framework
    :framework:ui["ui"]
  end
  subgraph :domain
    :domain:model["model"]
  end

  :framework:ui --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :framework:ui kotlin-multiplatform
class :domain:model kotlin-multiplatform

```