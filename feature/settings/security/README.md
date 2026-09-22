# :feature:settings:security

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
    :domain:repository["repository"]
    :domain:service["service"]
    :domain:service["service"]
    :domain:repository["repository"]
    :domain:usecase["usecase"]
  end
  subgraph :feature
    subgraph :settings
      :feature:settings:security["security"]
      :feature:settings:common["common"]
      :feature:settings:nav["nav"]
    end
    subgraph :authentication
      :feature:authentication:nav["nav"]
    end
  end

  :domain:usecase --> :domain:model
  :domain:usecase --> :domain:repository
  :domain:usecase --> :domain:service
  :domain:service --> :domain:model
  :domain:repository --> :domain:model
  :feature:settings:security --> :domain:usecase
  :feature:settings:security --> :feature:settings:common
  :feature:settings:security --> :feature:settings:nav
  :feature:settings:security --> :feature:authentication:nav

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :domain:usecase kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:service kotlin-multiplatform
class :feature:settings:security kotlin-multiplatform
class :feature:settings:common kotlin-multiplatform
class :feature:settings:nav kotlin-multiplatform
class :feature:authentication:nav kotlin-multiplatform

```