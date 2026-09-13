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
    :domain:service["service"]
    :domain:service["service"]
    :domain:model["model"]
    :domain:repository["repository"]
    :domain:usecase["usecase"]
    :domain:usecase["usecase"]
    :domain:repository["repository"]
  end

  :data:storage:device --> :data:storage
  :data:storage --> :domain:service
  :domain:service --> :domain:model
  :domain:service --> :domain:repository
  :domain:service --> :domain:usecase
  :domain:usecase --> :domain:model
  :domain:repository --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :data:storage:device kotlin-multiplatform
class :data:storage kotlin-multiplatform
class :domain:service kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:usecase kotlin-multiplatform

```