# :feature:collection:editor

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
    :domain:model["model"]
    :domain:usecase["usecase"]
    :domain:repository["repository"]
  end
  subgraph :feature
    subgraph :collection
      :feature:collection:nav["nav"]
      :feature:collection:editor["editor"]
      :feature:collection:nav["nav"]
    end
  end

  :domain:usecase --> :domain:model
  :domain:usecase --> :domain:repository
  :domain:usecase --> :domain:service
  :domain:service --> :domain:model
  :feature:collection:nav --> :domain:model
  :feature:collection:editor --> :domain:usecase
  :feature:collection:editor --> :feature:collection:nav
  :domain:repository --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :domain:usecase kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:service kotlin-multiplatform
class :feature:collection:nav kotlin-multiplatform
class :feature:collection:editor kotlin-multiplatform

```