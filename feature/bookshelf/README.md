# :feature:bookshelf

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :feature
    :feature:bookshelf["bookshelf"]
    subgraph :bookshelf
      :feature:bookshelf:nav["nav"]
      :feature:bookshelf:nav["nav"]
    end
    subgraph :folder
      :feature:folder:nav["nav"]
      :feature:folder:nav["nav"]
    end
    subgraph :settings
      :feature:settings:nav["nav"]
    end
  end
  subgraph :domain
    :domain:model["model"]
    :domain:usecase["usecase"]
    :domain:model["model"]
    :domain:usecase["usecase"]
  end

  :feature:bookshelf:nav --> :domain:model
  :domain:usecase --> :domain:model
  :feature:folder:nav --> :domain:model
  :feature:bookshelf --> :domain:usecase
  :feature:bookshelf --> :feature:bookshelf:nav
  :feature:bookshelf --> :feature:folder:nav
  :feature:bookshelf --> :feature:settings:nav

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :feature:bookshelf:nav kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :domain:usecase kotlin-multiplatform
class :feature:folder:nav kotlin-multiplatform
class :feature:bookshelf kotlin-multiplatform
class :feature:settings:nav kotlin-multiplatform

```