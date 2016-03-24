# msrv: Multi Selection support for RecyclerView

[![](https://jitpack.io/v/eneim/msrv.svg)](https://jitpack.io/#eneim/msrv)

<img src="https://raw.githubusercontent.com/eneim/msrv/develop/art/web_hi_res_512.png" width="256">

#### Setup

Prepare the following lines to your build.gradle

```guava
allprojects {
	repositories {
		// Other repos
		maven { url "https://jitpack.io" }
	}
}
	
ext {
  msrv_latest = '1.0.2' // TODO Always use the latest version from jitpack.io
}

dependencies {
	compile "com.github.eneim:msrv:${msrv_latest}"
}
```

***msrv_latest*** could be found here: [![](https://jitpack.io/v/eneim/msrv.svg)](https://jitpack.io/#eneim/msrv)

#### LICENSE

> Copyright 2016 eneim@Eneim Labs, nam@ene.im

> Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

>        http://www.apache.org/licenses/LICENSE-2.0
        
> Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
