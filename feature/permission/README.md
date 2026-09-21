# :feature:permission

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :feature:permission
    :feature:permission["permission"]
    :feature:permission:nav["nav"]
  end
  subgraph :framework
    :framework:permission["permission"]
  end

  :feature:permission --> :feature:permission:nav
  :feature:permission --> :framework:permission

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :feature:permission kotlin-multiplatform
class :feature:permission:nav kotlin-multiplatform
class :framework:permission kotlin-multiplatform

```
