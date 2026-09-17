# :framework:notification

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :framework
    :framework:notification["notification"]
  end
  subgraph :domain
    :domain:model["model"]
  end

  :framework:notification --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :framework:notification kotlin-multiplatform
class :domain:model kotlin-multiplatform

```
