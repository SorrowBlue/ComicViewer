# :feature:collection

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
    :domain:model["model"]
    :domain:usecase["usecase"]
  end
  subgraph :feature
    :feature:collection["collection"]
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
  :feature:folder:nav --> :domain:model
  :feature:book:nav --> :domain:model
  :feature:collection:nav --> :domain:model
  :feature:collection --> :domain:usecase
  :feature:collection --> :feature:book:nav
  :feature:collection --> :feature:collection:nav
  :feature:collection --> :feature:folder:nav
  :feature:collection --> :feature:settings:nav

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :domain:usecase kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :feature:folder:nav kotlin-multiplatform
class :feature:book:nav kotlin-multiplatform
class :feature:collection:nav kotlin-multiplatform
class :feature:collection kotlin-multiplatform
class :feature:settings:nav kotlin-multiplatform

```