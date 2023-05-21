# spock-study

## 依赖
Spock引入
```xml
<dependency>
  <groupId>org.codehaus.groovy</groupId>
  <artifactId>groovy-all</artifactId>
  <version>2.4.15</version>
</dependency>

<dependency>
  <groupId>org.spockframework</groupId>
  <artifactId>spock-spring</artifactId>
  <version>1.2-groovy-2.4</version>
  <scope>test</scope>
</dependency>
```
Spock自带Mock功能，可以用来Mock非静态方法，但是遇到静态方法时，我们需要导入powermock：
```xml
<!-- mock静态方法 -->
<dependency>
  <groupId>org.powermock</groupId>
  <artifactId>powermock-api-mockito2</artifactId>
  <version>2.0.0</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.powermock</groupId>
  <artifactId>powermock-module-junit4</artifactId>
  <version>2.0.0</version>
  <scope>test</scope>
</dependency>
```
如果需要测试dao层的sql语句，需要结合H2内存数据库使用，需要以下引入：
```xml
<!-- mock数据库 -->
<dependency>
  <groupId>com.github.janbols</groupId>
  <artifactId>spock-dbunit</artifactId>
  <version>0.4</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.dbunit</groupId>
  <artifactId>dbunit</artifactId>
  <version>2.5.1</version>
  <scope>test</scope>
</dependency>

<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <version>1.4.200</version>
  <scope>test</scope>
</dependency>
```
## 知识点
官方文档：[Spock](https://spockframework.org/spock/docs/2.3/spock_primer.html)
### Specification
每一个测试类都必须继承自spock.lang.Specification类。
```groovy
import spock.lang.Specification

class MyFirstSpecification extends Specification {
    
}
```
对于Spock，Specification代表了一个软件、应用、类的使用规范，其中所有的单元测试方法被称为feature.
### 成员变量
测试类中可以定义成员变量，且最好在定义的地方进行初始化，如下所示：
```groovy
import spock.lang.Specification

class MyFirstSpecification extends Specification {
    def obj = new ClassUnderSpecification()
    def coll = new Collaborator()
}
```
成员变量对每个feature不是共享的，即每个feature方法执行之前都会执行初始化，这样可以做到feature方法之间的隔离。
如果需要在每个feature方法中共享成员变量，需要在定义成员变量时添加@Shared注解,如下所示：
```groovy
import spock.lang.Specification

class MyFirstSpecification extends Specification {
    @Shared def obj = new ClassUnderSpecification()
}
```
对于静态变量，应该仅仅用在定义常量的时候：
```groovy
import spock.lang.Specification

class MyFirstSpecification extends Specification {
    static final PI = 3.1415926
}
```
### Fixture Methods
#### 介绍
Spock中有以下Fixture方法，用来进行设置和清理。
```groovy
def setupSpec() {}  // runs once - before the first feature method
def setup() {}      // runs before every feature method
def cleanup() {}    // runs after every feature method
def cleanupSpec     // runs once - after the last feature method
```
Fixture方法是可选的。一般来说，对于每个feature方法，最好使用setup()和cleanup()方法进行设置和清理。
如果需要每个feature方法之间共享fixture，那么可以使用setupSpec()和cleanupSpec(), 需要注意的是，setupSpec()和cleanupSpec()方法中引用的成员变量必须
加@Shared注解。
#### 调用顺序
Specification的子类可以覆盖了fixture方法，需要注意父子类中fixture方法的调用顺序：
1. super.setupSpec
2. sub.setupSpec
3. super.setup
4. sub.setup
5. feature method
6. sub.cleanup
7. super.cleanup
8. sub.cleanupSpec
9. super.cleanupSpec

### Feature方法
#### 定义
feature方法的方法名由字符串定义，可以包含任何字符，如下所示：
```groovy
def "pushing an element on the stack"() {
    
}

def "身份证号工具类测试"() {
}
```
#### Phases
一个feature方法包含以下四个阶段（Phase）：
1. set up
2. stimulus
3. response
4. cleanup

其中，第1和第4阶段可选。

#### Blocks
Spock通过内置的Blocks来实现上面的四个阶段，Blocks与Phases的对应关系如下：

| Blocks | Phases |
| ------ | ------ |
| given: | Setup |
| when: | Stimulus |
| then: | Response |
| expect: | Stimulus, Response |
| cleanup: | Cleanup |
| where: | Setup, Stimulus, Response, Cleanup |

在方法名与第一个显示定义的Block之间的代码默认为 given Block
每个方法至少有一个Block，且Block之间不能嵌套。

##### given
```groovy
given:
    def stack = new Stack()
    def elem = "push me"
```
##### When & Then
```groovy
when:  // stimulus
    stack.push(elem)

then:  // response
    !stack.empty
    stack.size() == 1
    stack.peek() == elem
```
##### Expect
Except块支持在一个表达是中调用方法，并指定期望的返回值，相当于when和then
```groovy
when:
    def x = Math.max(1,2)
then:
    x == 2

相当于

expect:
    Math.max(1,2) == 2
```
##### Cleanup
```groovy
given:
    def file = new File("/some/path")
    file.createNewFile()

cleanup:
    file.delete()
```

##### Where
Where块一般出现在feature方法的最后，用来描述数据驱动的feature方法，用法如下所示：
```groovy
def "computing the maximum of tow numbers"() {
    expect:
        Math.max(a, b) == c
    
    where:
        a << [5, 3]
        b << [1, 9]
        c << [5, 9]
}
```
或者
```groovy
def "computing the maximum of tow numbers"() {
    expect:
        Math.max(a, b) == c
    
    where:
        a | b || c
        5 | 3 || 5
        3 | 9 || 9
}
```
##### with
```groovy
def service = Mock(Service) // has start(), stop(), and doWork() methods
def app = new Application(Service) //controls the lifecycle of the service

when:
    app.run()

then:
    with(service) {
        1 * start()
        1 * doWork()
        1 * stop()
    }
```
#### Extensions
1. @Timeout: 指定feature方法执行的超时时间
2. @Ignore: 忽略@Ignore标注的feature方法
3. @IgnoreRest: 忽略被@IgnoreRest标注的feature方法外的其它feature方法
4. @FailsWith:
5. ...

详见文档：[Extensions](https://spockframework.org/spock/docs/2.3/extensions.html#extensions)
