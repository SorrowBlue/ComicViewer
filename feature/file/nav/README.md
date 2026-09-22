# :feature:file:nav

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :feature
    subgraph :file
      :feature:file:nav["nav"]
    end
  end
  subgraph :domain
    :domain:model["model"]
  end

  :feature:file:nav --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :feature:file:nav kotlin-multiplatform
class :domain:model kotlin-multiplatform

```