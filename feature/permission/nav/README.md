# :feature:permission:nav

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :feature:permission
    :feature:permission:nav["nav"]
  end
  subgraph :framework
    :framework:navigation["navigation"]
  end

  :feature:permission:nav --> :framework:navigation

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :feature:permission:nav kotlin-multiplatform
class :framework:navigation kotlin-multiplatform

```
