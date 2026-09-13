# :data:storage:device

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :data
    :data:storage["storage"]
    :data:storage["storage"]
    subgraph :storage
      :data:storage:device["device"]
    end
  end
  subgraph :domain
    :domain:repository["repository"]
    :domain:service["service"]
    :domain:service["service"]
    :domain:model["model"]
    :domain:repository["repository"]
  end

  :data:storage:device --> :data:storage
  :data:storage --> :domain:repository
  :data:storage --> :domain:service
  :domain:service --> :domain:model
  :domain:repository --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :data:storage:device kotlin-multiplatform
class :data:storage kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:service kotlin-multiplatform
class :domain:model kotlin-multiplatform

```