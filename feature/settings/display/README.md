# :feature:settings:display

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
      :feature:settings:display["display"]
      :feature:settings:common["common"]
      :feature:settings:nav["nav"]
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

  :feature:settings:display --> :domain:usecase
  :feature:settings:display --> :feature:settings:common
  :feature:settings:display --> :feature:settings:nav
  :domain:usecase --> :domain:model
  :domain:usecase --> :domain:repository
  :domain:usecase --> :domain:service
  :domain:service --> :domain:model
  :domain:repository --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :feature:settings:display kotlin-multiplatform
class :domain:usecase kotlin-multiplatform
class :feature:settings:common kotlin-multiplatform
class :feature:settings:nav kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:service kotlin-multiplatform

```