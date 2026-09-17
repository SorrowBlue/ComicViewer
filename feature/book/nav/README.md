# :feature:book:nav

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :feature
    subgraph :book
      :feature:book:nav["nav"]
    end
  end
  subgraph :domain
    :domain:model["model"]
  end

  :feature:book:nav --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :feature:book:nav kotlin-multiplatform
class :domain:model kotlin-multiplatform

```
