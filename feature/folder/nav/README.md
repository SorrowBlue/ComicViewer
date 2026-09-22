# :feature:folder:nav

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :feature
    subgraph :folder
      :feature:folder:nav["nav"]
    end
  end
  subgraph :domain
    :domain:model["model"]
  end

  :feature:folder:nav --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :feature:folder:nav kotlin-multiplatform
class :domain:model kotlin-multiplatform

```