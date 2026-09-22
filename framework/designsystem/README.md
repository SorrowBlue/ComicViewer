# :framework:designsystem

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :framework
    :framework:designsystem["designsystem"]
  end
  subgraph :domain
    :domain:model["model"]
  end

  :framework:designsystem --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :framework:designsystem kotlin-multiplatform
class :domain:model kotlin-multiplatform

```