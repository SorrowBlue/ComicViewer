# :framework:permission

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :framework
    :framework:permission["permission"]
  end
  subgraph :domain
    :domain:model["model"]
  end

  :framework:permission --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :framework:permission kotlin-multiplatform
class :domain:model kotlin-multiplatform

```
