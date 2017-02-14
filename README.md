# Spring-Demo-JQuery-Ajax  
> *웹 서비스를 만들 때 자주 사용되는 비동기 통신 기술인 AJAX를 스프링 프레임워크와 연계하여 활용하는 다양한 방식에 대해서 알아보고자 합니다.*  

XHR(XMLHttpRequest)를 직접적으로 이용하는 것보다는 JQuery에서 지원하는 AJAX(Asynchronos Javascript And XML)기능을 활용하도록 하는 것이 나을 것 같다는 생각입니다. 
- [XMLHttpRequest, Mozilla Developer Network](https://developer.mozilla.org/ko/docs/XMLHttpRequest)  
- [Ajax, JQuery](http://api.jquery.com/jquery.ajax/)  

JQuery 이외에도 ajax를 지원하는 다양한 라이브러리가 존재합니다. 예를 들어, [Prototypejs](http://prototypejs.org/learn/introduction-to-ajax)도 많이 사용되는 유틸성 라이브러리 중 하나입니다. 그러나 저와 같은 초보자 및 신입 개발자들은 JQuery에 익숙하므로 JQuery가 지원하는 Ajax 기능에 대해서 먼저 살펴보고자 합니다.

## JQuery.ajax 형태 바로알기  
제이쿼리에서 제공하는 ajax 는 다음과 같은 구조로 구성되어 있습니다. 물론 이외에도 생략된 다양한 프로퍼티들이 존재하므로 더 찾아보시면 좋을 것 같습니다. 아래의 형태는 아마도 자주 사용되는 프로퍼티만 모아놓은 부분이라고 할 수 있습니다. 
```js
$.ajax({
  type	: "GET", //요청 메소드 타입
  url		: "url", //요청 경로
  async : true, //비동기 여부
  data  : {key : value}, //요청 시 포함되어질 데이터
  processData : true, //데이터를 컨텐트 타입에 맞게 변환 여부
  cache : true, //캐시 여부
  contentType : "application/json", //요청 컨텐트 타입 "application/x-www-form-urlencoded; charset=UTF-8"
  dataType	: "json", //응답 데이터 형식 명시하지 않을 경우 자동으로 추측
  beforeSend  : function(){
    // XHR Header를 포함해서 HTTP Request를 하기전에 호출됩니다.
  },
  success	: function(data, status, xhr){
    // 정상적으로 응답 받았을 경우에는 success 콜백이 호출되게 됩니다.
    // 이 콜백 함수의 파라미터에서는 응답 바디, 응답 코드 그리고 XHR 헤더를 확인할 수 있습니다.
  },
  error	: function(xhr, status, error){
  	// 응답을 받지 못하였다거나 정상적인 응답이지만 데이터 형식을 확인할 수 없기 때문에 error 콜백이 호출될 수 있습니다.
  	// 예를 들어, dataType을 지정해서 응답 받을 데이터 형식을 지정하였지만, 서버에서는 다른 데이터형식으로 응답하면  error 콜백이 호출되게 됩니다.
  },
  complete : function(xhr, status){
    // success와 error 콜백이 호출된 후에 반드시 호출됩니다.
    // try - catch - finally의 finally 구문과 동일합니다.
  }
});
```
GET과 POST의 차이는 무엇인지 아시나요? 바로 데이터가 어떻게 포함되는가 입니다. POST 요청시에 URL에 파라미터가 보이지 않는 이유는 데이터가 요청 바디에 포함되기 때문입니다. 그렇기 때문에 GET과 POST에 따라 데이터를 URL에 추가해야할지 요청 바디에 추가해야할지를 알아야 하는 것입니다. processData는 GET 요청인데 data에 오브젝트가 지정될 경우에 요청하기전에 그 데이터를 파라미터 형식으로 URL에 추가해주는 역할을 하게 됩니다. contentType은 요청 시에 컨텐트 타입을 지정하는 것이고 dataType은 응답 시에 컨텐트 타입을 지정하는 것입니다. 

이외에도 JQuery에서는 [다양한 ajax 기능](https://api.jquery.com/category/ajax/)을 제공하고 있습니다. 만약, 스프링 시큐리티를 적용해서 HTTP 통신시에 CSRF 토큰이 필요하다면 다음과 같이 XHR Header에 CSRF 토큰을 추가해서 보낼 수 있습니다.

```js
// 스프링 시큐리티 태그라이브러리로 메타 태그에 토큰 정보를 적용했다는 가정입니다.
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$.ajaxSetup({
   beforeSend: function(xhr) {
    xhr.setRequestHeader(header, token);
   }
});
```

## Spring Controller의 구조 바로 알기  
스프링 프레임워크에서는 ajax 통신을 위해서 스프링 @MVC로 다양한 어노테이션을 지원합니다. 다양한 어노테이션을 확인하면서 구조를 익혀보도록 하겠습니다. 스프링 애플리케이션은 기본적으로 뷰 리졸버를 통해서 요청에 대한 응답을 하게 됩니다. 일반적인 HTTP 요청의 경우에는 JstlView로써 응답을 하게 되지만, XHR 요청에 의해서 다양한 데이터 형식으로 응답하기 위한 메시지 컨버터라는 것을 지원합니다.  
```text
Message Converter List
- StringHttpMessageConverter
- FormHttpMessageConverter
- ByteArrayMessageConverter
- MarshallingHttpMessageConverter
- MappingJacksonHttpMessageConverter
- MappingJackson2HttpMessageConverter
- SourceHttpMessageConverter
- BufferedImagedHttpMessageConverter
```

우리가 자주 응답해야하는 데이터 형식은 JSON 형식일 것입니다. 예를 들어 JSON 형식으로 응답하기 위해서는 뷰 리졸버로써 ModelAndView를 생성해서 반환하면 됩니다. 그러나 스프링 3.1 이상부터는 jackson 라이브러리를 의존성으로 추가할 경우에 자동적으로 MappringJacksonConverter를 적용해줍니다. 특히, 스프링 3.1.2 부터는 com.fasterxml.jackson 라이브러리에 대해서 MappingJackson2HttpMessageConverter가 등록됨을 알고 있어야 합니다. 이 말은 스프링 버전에 따라서 메시지 컨버터가 다를 수 있다는 말이 됩니다. 따라서, 왠만하면은 스프링의 버전을 3.1.2 이상으로 하는게 심적으로 나을 듯 싶습니다.

스프링 부트에서는 이러한 부분도 관리해주므로 추가적으로 jackson 라이브러리를 pom.xml에 추가할 필요가 없습니다.  

> *본 프로젝트에서는 ModelAndView로써 응답하는 방식은 설명하지 않겠습니다. [여기서 확인하도록 합시다](http://www.nextree.co.kr/p11205/)*  

### @RequestMapping  
@RequestMapping에는 요청과 응답과 관련한 프로퍼티를 설정할 수 있습니다.  produces와 consumes는 확실히 알고 넘어가셔야 합니다.  

- method=RequestMethod.GET  
method는 어떠한 요청 타입을 처리할 것인가를 결정하는 부분입니다.
- produces=MediaType.APPLICATION_JSON_VALUE  
produces는 어떠한 데이터 형식으로 응답할 것인가를 결정하는 부분입니다.
- consumes=MediaType.APPLICATION_JSON_VALUE  
consumes는 어떠한 요청에 대해서 처리할 것인가를 결정하는 부분입니다.

### @ModelAttribute, @ReqeustParam  
이 두개의 어노테이션은 GET과 DELETE 요청에서 활용할 수 있습니다. 그 이유는 파라미터 값을 확인해서 데이터를 바인딩해주기 때문입니다. @RequestParam은 request.getParameter()로써 가져오는 반면에 @ModelAttribute는 자바 클래스의 Getter, Setter에 의해 데이터를 바인딩시키는 것입니다. 그렇기 때문에 만약 객체 단위로 바인딩하고 싶다면 @ModelAttribute를  이용해야 한다는 것입니다.

> *직접 확인하고 싶으시다면 본 프로젝트를 동작시켜 ajaxList와 ajaxListModel의 차이를 확인하시기 바랍니다.*  

### @RequestBody  
이 어노테이션은 POST와 PUT 처럼 데이터가 HTTP 요청 바디에 포함되는 경우에 이를 확인해서 데이터를 바인딩 해줍니다. 이 어노테이션의 중요한 부분은 GET 요청과 같이 파라미터를 통해 제공되는 데이터는 바인딩할 수 없다는 점입니다.

> *직접 확인하고 싶으시다면 본 프로젝트를 동작시켜 ajaxMap와 ajaxMapGet의 차이를 확인하시기 바랍니다. ajaxMapGet의 요청이 왜 실패하는지에 대해서 서버측 로그를 살펴보시기 바랍니다.*  

### @ResponseBody  
이 어노테이션은 응답되는 데이터에 대하여 등록된 메시지 컨버터를 통해 변환시켜 응답하게 됩니다. 따라서, 뷰에 모델로서 데이터를 추가시켜 응답하는 것이 아니라 데이터를 HTTP 본문으로 응답하게 된다는 것입니다.  

> *ajaxList와 ajaxListNobody를 통해서 @ResponseBody가 있을 경우랑 없을 경우를 비교해보세요. 왜 @ResponseBody가 없을 때 ViewResolver를 찾는 것 같나요?*

## Test Case
본 프로젝트에서 현재 진행한 테스트 케이스는 다음과 같습니다.  

#### 1. GET, @ResponseBody와 @ModelAttribute, @RequestParm을 확인할 수 있는 케이스  
#### 2. GET, 1번과 동일하나 @ModelAttribute를 통해서 객체 단위로 바인딩하는 것을 확인할 수 있는 케이스  
#### 3. GET, 1번과 동일하나 @ResponseBody를 지정하지 않았을 경우를 확인할 수 있는 케이스  

#### 4. PUT, @ResponseBody와 @RequestBody를 지정했을 경우를 확인할 수 있는 케이스  
#### 5. GET, 4번과 동일하나 GET 요청에 @RequestBody를 지정했을 경우를 확인할 수 있는 케이스  

#### 6. POST, ResponseEntity를 통해서 HttpStatus도 지정할 수 있는 것을 확인하는 케이스  
#### 7. POST, 6번과 동일하나 @ResponseBody를 지정하지 않을 경우를 확인할 수 있는 케이스  
#### 8. POST, 7번과 동일하나 URL에 파라미터를 함께 요청시에 @RequestParam 지원여부를 확인할 수 있는 케이스  

> *6번과 7번은 의아해하실 수 있으실 겁니다. 이와 관련된 정보는 [여기](http://okky.kr/article/311196)에서 확인하실 수 있습니다. 간단히 말하면 ResponseEntity는 응답 헤더와 바디를 가지는 객체를 응답하는 것이라고 보면 됩니다.*

## 마지막으로 초보자들이 잘못 사용하거나 접근하는 경우를 알아봅시다.  
#### 1. 저는 제대로 구현한 것 같은데 에러로 응답받습니다.  
- dataType을 지정한 뒤 그 형식으로 응답하지 않는다면 정상적으로 응답해도 에러 콜백이 호출될 가능성이 있습니다. 예를 들어, 스프링 컨트롤러에서는 문자열이나 null을 응답하는데 ajax에서는 json으로 지정할 경우에는 서버에서는 정상적으로 응답되지만 클라이언트에서는 해당 데이터를 json으로 파싱할 수 없기 때문에 에러 콜백이 호출됩니다.  

#### 2. 데이터를 서버에서 받아올 수 없습니다.  
- 요청 메소드 타입이 GET일 때 processData를 false로 지정할 경우에는 데이터를 url에 직접 포함시켜줘야 합니다.  만약, POST같은 요청의 경우에는 데이터가 요청 바디에 포함되어져야 한다는 것을 잊지 마시기 바랍니다.

#### 3. GET 요청을 통해 보내는데 일정 데이터 크기이상 보내지지 않습니다.  
- 브라우저별로 URL의 지원 크기가 다릅니다. 그렇기 때문에 POST로 요청 바디에 데이터를 포함시켜 보내는 이유도 바로 이 때문입니다. 물론 서버 측에서도 요청 바디에 대한 크기를 제한할 수 도 있습니다. 

#### 4. 406 - Not Acceptable 응답해요 ㅠㅠ  [2017-02-14]
- 혹시 @ResponseBody로 List나 Map을 응답하고 계신가요? 응답하기 위해서는 annation-driven 설정이 되어있어야 하고 요청하는 응답 데이터로 변환할 수 있도록 메시지 컨버터가 필요합니다. annotation-driven 설정이 안되있거나 MessageHttpConverter를 등록합시다.

## Run As - Spring Boot App - http://localhost:8080/ 
