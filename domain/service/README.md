# :domain:service

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :domain
    :domain:service["service"]
    :domain:model["model"]
  end

  :domain:service --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :domain:service kotlin-multiplatform
class :domain:model kotlin-multiplatform

```