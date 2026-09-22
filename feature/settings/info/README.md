# :feature:settings:info

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :feature
    subgraph :settings
      :feature:settings:info["info"]
      :feature:settings:common["common"]
      :feature:settings:nav["nav"]
    end
    subgraph :tutorial
      :feature:tutorial:nav["nav"]
    end
  end
  subgraph :domain
    :domain:usecase["usecase"]
    :domain:usecase["usecase"]
    :domain:model["model"]
    :domain:repository["repository"]
    :domain:service["service"]
    :domain:service["service"]
    :domain:repository["repository"]
  end

  :feature:settings:info --> :domain:usecase
  :feature:settings:info --> :feature:settings:common
  :feature:settings:info --> :feature:settings:nav
  :feature:settings:info --> :feature:tutorial:nav
  :domain:usecase --> :domain:model
  :domain:usecase --> :domain:repository
  :domain:usecase --> :domain:service
  :domain:service --> :domain:model
  :domain:repository --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :feature:settings:info kotlin-multiplatform
class :domain:usecase kotlin-multiplatform
class :feature:settings:common kotlin-multiplatform
class :feature:settings:nav kotlin-multiplatform
class :feature:tutorial:nav kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:service kotlin-multiplatform

```