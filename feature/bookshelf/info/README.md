# :feature:bookshelf:info

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
      :feature:bookshelf:info["info"]
      :feature:bookshelf:nav["nav"]
      :feature:bookshelf:nav["nav"]
    end
    subgraph :permission
      :feature:permission:nav["nav"]
      :feature:permission:nav["nav"]
    end
  end
  subgraph :domain
    :domain:usecase["usecase"]
    :domain:model["model"]
    :domain:usecase["usecase"]
    :domain:model["model"]
    :domain:repository["repository"]
    :domain:service["service"]
    :domain:service["service"]
    :domain:repository["repository"]
  end

  :feature:bookshelf:info --> :domain:usecase
  :feature:bookshelf:info --> :feature:bookshelf:nav
  :feature:bookshelf:info --> :feature:permission:nav
  :feature:bookshelf:nav --> :domain:model
  :domain:usecase --> :domain:model
  :domain:usecase --> :domain:repository
  :domain:usecase --> :domain:service
  :domain:service --> :domain:model
  :domain:repository --> :domain:model
  :feature:permission:nav --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :feature:bookshelf:info kotlin-multiplatform
class :domain:usecase kotlin-multiplatform
class :feature:bookshelf:nav kotlin-multiplatform
class :feature:permission:nav kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:service kotlin-multiplatform

```