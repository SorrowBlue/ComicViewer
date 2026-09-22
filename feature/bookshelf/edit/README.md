# :feature:bookshelf:edit

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :feature
    subgraph :bookshelf
      :feature:bookshelf:nav["nav"]
      :feature:bookshelf:edit["edit"]
      :feature:bookshelf:nav["nav"]
    end
    subgraph :permission
      :feature:permission:nav["nav"]
      :feature:permission:nav["nav"]
    end
  end
  subgraph :domain
    :domain:model["model"]
    :domain:usecase["usecase"]
    :domain:model["model"]
    :domain:repository["repository"]
    :domain:service["service"]
    :domain:service["service"]
    :domain:repository["repository"]
    :domain:usecase["usecase"]
  end

  :feature:bookshelf:nav --> :domain:model
  :domain:usecase --> :domain:model
  :domain:usecase --> :domain:repository
  :domain:usecase --> :domain:service
  :domain:service --> :domain:model
  :domain:repository --> :domain:model
  :feature:permission:nav --> :domain:model
  :feature:bookshelf:edit --> :domain:usecase
  :feature:bookshelf:edit --> :feature:bookshelf:nav
  :feature:bookshelf:edit --> :feature:permission:nav

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :feature:bookshelf:nav kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :domain:usecase kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:service kotlin-multiplatform
class :feature:permission:nav kotlin-multiplatform
class :feature:bookshelf:edit kotlin-multiplatform

```