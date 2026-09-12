# :feature:tutorial

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
    :domain:usecase["usecase"]
  end
  subgraph :feature
    :feature:tutorial["tutorial"]
    subgraph :tutorial
      :feature:tutorial:nav["nav"]
    end
  end

  :domain:usecase --> :domain:model
  :feature:tutorial --> :domain:usecase
  :feature:tutorial --> :feature:tutorial:nav

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :domain:usecase kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :feature:tutorial kotlin-multiplatform
class :feature:tutorial:nav kotlin-multiplatform

```