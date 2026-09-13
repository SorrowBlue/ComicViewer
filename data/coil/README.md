# :data:coil

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
    :data:coil["coil"]
    :data:storage["storage"]
  end
  subgraph :domain
    :domain:repository["repository"]
    :domain:repository["repository"]
    :domain:repository["repository"]
    :domain:model["model"]
  end

  :data:storage --> :domain:repository
  :data:coil --> :domain:repository
  :data:coil --> :data:storage
  :domain:repository --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :data:storage kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :data:coil kotlin-multiplatform
class :domain:model kotlin-multiplatform

```