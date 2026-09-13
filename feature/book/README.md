# :feature:book

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :domain
    :domain:usecase["usecase"]
    :domain:model["model"]
    :domain:model["model"]
    :domain:usecase["usecase"]
  end
  subgraph :feature
    :feature:book["book"]
    subgraph :book
      :feature:book:nav["nav"]
      :feature:book:nav["nav"]
    end
    subgraph :settings
      :feature:settings:nav["nav"]
    end
  end

  :domain:usecase --> :domain:model
  :feature:book:nav --> :domain:model
  :feature:book --> :domain:usecase
  :feature:book --> :feature:book:nav
  :feature:book --> :feature:settings:nav

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :domain:usecase kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :feature:book:nav kotlin-multiplatform
class :feature:book kotlin-multiplatform
class :feature:settings:nav kotlin-multiplatform

```