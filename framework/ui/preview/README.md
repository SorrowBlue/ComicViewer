# :feature:tutorial:nav

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :framework
    subgraph :ui
      :framework:ui:preview["preview"]
    end
  end
  subgraph :domain
    :domain:model["model"]
  end

  :framework:ui:preview --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :framework:ui:preview kotlin-multiplatform
class :domain:model kotlin-multiplatform

```