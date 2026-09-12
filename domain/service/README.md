# :domain:service

## Module dependency graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph TB
  subgraph :domain
    :domain:service["service"]
    :domain:model["model"]
    :domain:repository["repository"]
    :domain:usecase["usecase"]
    :domain:usecase["usecase"]
    :domain:repository["repository"]
  end

  :domain:service --> :domain:model
  :domain:service --> :domain:repository
  :domain:service --> :domain:usecase
  :domain:usecase --> :domain:model
  :domain:repository --> :domain:model

classDef kotlin-multiplatform fill:#C792EA,stroke:#fff,stroke-width:2px,color:#fff;
class :domain:service kotlin-multiplatform
class :domain:model kotlin-multiplatform
class :domain:repository kotlin-multiplatform
class :domain:usecase kotlin-multiplatform

```