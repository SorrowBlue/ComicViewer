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
    :data:coil["coil"]
  end
  subgraph :domain
    :domain:repository["repository"]
    :domain:repository["repository"]
    :domain:model["model"]
  end

  :data:coil --> :domain:repository
  :domain:repository --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :data:coil kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:model kotlin-multiplatform

```