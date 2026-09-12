# :domain:repository

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :domain
    :domain:repository["repository"]
    :domain:model["model"]
  end

  :domain:repository --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :domain:repository kotlin-multiplatform
class :domain:model kotlin-multiplatform

```