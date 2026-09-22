# :feature:readlater

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
    :feature:readlater["readlater"]
    subgraph :file
      :feature:file:nav["nav"]
      :feature:file:nav["nav"]
    end
    subgraph :folder
      :feature:folder:nav["nav"]
      :feature:folder:nav["nav"]
    end
    subgraph :book
      :feature:book:nav["nav"]
      :feature:book:nav["nav"]
    end
    subgraph :collection
      :feature:collection:nav["nav"]
      :feature:collection:nav["nav"]
    end
    subgraph :settings
      :feature:settings:nav["nav"]
    end
  end

  :domain:usecase --> :domain:model
  :domain:usecase --> :domain:repository
  :domain:usecase --> :domain:service
  :domain:service --> :domain:model
  :feature:file:nav --> :domain:model
  :feature:folder:nav --> :domain:model
  :feature:book:nav --> :domain:model
  :feature:collection:nav --> :domain:model
  :feature:readlater --> :domain:usecase
  :feature:readlater --> :feature:book:nav
  :feature:readlater --> :feature:collection:nav
  :feature:readlater --> :feature:file:nav
  :feature:readlater --> :feature:folder:nav
  :feature:readlater --> :feature:settings:nav
  :domain:repository --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :domain:usecase kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:service kotlin-multiplatform
class :feature:file:nav kotlin-multiplatform
class :feature:folder:nav kotlin-multiplatform
class :feature:book:nav kotlin-multiplatform
class :feature:collection:nav kotlin-multiplatform
class :feature:readlater kotlin-multiplatform
class :feature:settings:nav kotlin-multiplatform

```