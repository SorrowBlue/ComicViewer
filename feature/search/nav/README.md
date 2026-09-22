# :feature:search:nav

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :feature
    subgraph :search
      :feature:search:nav["nav"]
    end
  end
  subgraph :domain
    :domain:model["model"]
  end

  :feature:search:nav --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :feature:search:nav kotlin-multiplatform
class :domain:model kotlin-multiplatform

```