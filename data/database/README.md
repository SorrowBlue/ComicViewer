# :data:database

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
    :domain:repository["repository"]
    :domain:service["service"]
    :domain:repository["repository"]
  end
  subgraph :data
    :data:database["database"]
  end

  :domain:service --> :domain:model
  :data:database --> :domain:repository
  :data:database --> :domain:service
  :domain:repository --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :domain:service kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :data:database kotlin-multiplatform
class :domain:repository kotlin-multiplatform

```