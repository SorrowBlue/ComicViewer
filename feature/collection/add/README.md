# :feature:collection:add

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
    :domain:repository["repository"]
    :domain:usecase["usecase"]
  end
  subgraph :feature
    subgraph :collection
      :feature:collection:nav["nav"]
      :feature:collection:add["add"]
      :feature:collection:nav["nav"]
    end
  end

  :domain:usecase --> :domain:model
  :domain:usecase --> :domain:repository
  :domain:usecase --> :domain:service
  :domain:service --> :domain:model
  :feature:collection:nav --> :domain:model
  :domain:repository --> :domain:model
  :feature:collection:add --> :domain:usecase
  :feature:collection:add --> :feature:collection:nav

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :domain:usecase kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:service kotlin-multiplatform
class :feature:collection:nav kotlin-multiplatform
class :feature:collection:add kotlin-multiplatform

```