# :framework:startup

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :framework
    :framework:startup["startup"]
  end
  subgraph :domain
    :domain:model["model"]
  end

  :framework:startup --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :framework:startup kotlin-multiplatform
class :domain:model kotlin-multiplatform

```
