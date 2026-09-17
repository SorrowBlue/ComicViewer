# :feature:file

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
    :feature:file["file"]
    subgraph :file
      :feature:file:nav["nav"]
      :feature:file:nav["nav"]
    end
    subgraph :folder
      :feature:folder:nav["nav"]
      :feature:folder:nav["nav"]
    end
    subgraph :collection
      :feature:collection:nav["nav"]
      :feature:collection:nav["nav"]
    end
  end

  :domain:usecase --> :domain:model
  :domain:usecase --> :domain:repository
  :domain:usecase --> :domain:service
  :domain:service --> :domain:model
  :feature:file:nav --> :domain:model
  :feature:folder:nav --> :domain:model
  :feature:collection:nav --> :domain:model
  :domain:repository --> :domain:model
  :feature:file --> :domain:usecase
  :feature:file --> :feature:file:nav
  :feature:file --> :feature:collection:nav
  :feature:file --> :feature:folder:nav

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :domain:usecase kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:service kotlin-multiplatform
class :feature:file:nav kotlin-multiplatform
class :feature:folder:nav kotlin-multiplatform
class :feature:collection:nav kotlin-multiplatform
class :feature:file kotlin-multiplatform

```
