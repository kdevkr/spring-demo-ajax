*웹 서비스를 만들 때 자주 사용되는 비동기 통신 기술인 AJAX의 사용방법에 대하여 알아보도록 합시다.*

## [JQuery-Ajax](http://api.jquery.com/jquery.ajax/)
일반적인 HTTP 프로토콜은 요청과 응답에 대해서 상태를 유지하지 않는 비 연결성 프로토콜입니다. 그래서 새로운 데이터를 제공받기 위해서는 다시 요청을 응답을 받기 때문에 브라우저가 새로 갱신되는 단점이 생기곤 하였습니다. 이러한 불편한 점을 보완하기 위해서 다양한 방법이 제시되었으나 최근에는 JQuery에서 지원하는 XHR(XMLHttpRequest)로 구성된 AJAX(Asynchronos Javascript And XML)기술이 많이 사용되고 있습니다.  

## Ajax 기본 구조  
JQuery에서 지원하는 ajax는 다음과 같은 형태로 사용됩니다.
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
    // HTTP 요청 전에 호출된다
  },
  success	: function(data, status, xhr){
    // 응답 파라미터를 하나로 지정 가능
    // 정상적으로 응답 받았을 경우에 요청된다
  },
  error	: function(xhr, status, error){
    // 응답 파라미터를 하나로 지정 가능
    // 응답을 받지 못하였거나, 정상적인 응답이나 데이터 형식이 dataType과 같지 않아서 파싱하지 못했을 경우에 호출된다.
  },
  complete : function(xhr, status){
    // 응답 파라미터를 하나로 지정 가능
    // success와 error 콜백이 호출된 후에 반드시 호출된다. finally라고 보면 된다
  }
});
```

## Spring Controller 구조  
스프링 프레임워크에서는 ajax 통신을 위한 다양한 어노테이션을 지원합니다. 특히, 스프링 3.1.2 부터는 com.fasterxml.jackson 라이브러리를 사용할 경우에는 자동적으로 MappingJackson2HttpMessageConverter가 등록됨을 알아야 할 듯 싶습니다.

이 말은 스프링 버전이 3.1.1 이하일 경우에는 MappingJacksonHttpMessageConverter를 직접적으로 등록하고 Jackson 라이브러리 버전을 지원하는지 확인해야 함을 주의해야 한다는 것입니다.  

스프링 부트에서는 이러한 부분도 관리해주므로 추가적으로 jackson 라이브러리를 pom.xml에 추가할 필요가 없습니다.  

```java
/**
 * produces : 해당 미디어 타입으로 응답할 수 있도록 한다.
 * consumes : 해당 요청만 처리할 수 있도록 한다. 그렇기 때문에 ajax의 contentType을 반드시 명시해야만 한다.
 * @ModelAttribute : 파라미터의 데이터를 객체 단위 또는 필드 단위로 바인딩 해준다. 필드명이 중복되지 않을 경우에는 생략 가능하다.
 * @RequestParam : 파라미터의 데이터를 필드 단위로 바인딩 해준다.
 * @RequestBody : Body에 존재하는 데이터를 해당 객체로 바인딩 해준다.
 * @ResponseBody : Body에 대해서 컨텐트 타입에 따라 메시지 컨버터로 변환시켜 응답한다.
 * Message Converter :
 * - StringHttpMessageConverter
 * - FormHttpMessageConverter
 * - ByteArrayMessageConverter
 * - MarshallingHttpMessageConverter
 * - MappingJacksonHttpMessageConverter
 * - SourceHttpMessageConverter
 * - BufferedImagedHttpMessageConverter
 */
```

- List를 응답하는 컨트롤러  
consumes로 요청 형식을 application/json으로 지정합니다.  ajax로 해당 경로의 요청을 할 경우에는 contentType을 명시해야합니다.
```java
@ResponseBody
@RequestMapping(value="/list", method=RequestMethod.GET, consumes=MediaType.APPLICATION_JSON_VALUE)
public List<String> list(@ModelAttribute("username") String username, @RequestParam("password") String password){
  List<String> response = new ArrayList<String>();
  response.add(username);
  response.add(password);
  return response;
}
```
- Map을 응답하는 컨트롤러  
produces로 응답 형식을 json으로 지정합니다.  
```java
@ResponseBody
@RequestMapping(value="/map", method=RequestMethod.PUT, produces=MediaType.APPLICATION_JSON_VALUE)
public Map<String, Object> map(@RequestBody DataSet dataSet){
  Map<String, Object> response = new HashMap<String, Object>();
  response.put("username", dataSet.getUsername());
  response.put("password", dataSet.getPassword());
  return response;
}
```
- Entity를 응답하는 컨트롤러  
```java
@ResponseBody
@RequestMapping(value="/entity", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<Object> entity(@RequestBody DataSet dataSet){
  Map<String, Object> map = new HashMap<String, Object>();
  map.put("username", dataSet.getUsername());
  map.put("password", dataSet.getPassword());
  ResponseEntity<Object> response = new ResponseEntity<Object>(map, HttpStatus.OK);
  return response;
}
```

*꼭 이렇게만 해야한다는 것은 아닙니다. 어떠한 방식으로 요청을 하고 응답을 하는지 알고자 할 뿐입니다.*

## Front Ajax 요청 구조  
- List 컨트롤러로 요청할 경우  
URL에 파라미터를 포함시켰지만 processData를 true로 지정한 후 data에 데이터를 추가할 경우에는 자동적으로 파라미터형식으로 URL에 추가해서 전송합니다.
```js
function ajaxList(){
var dataSet = new Object();
dataSet.username = "kdevkr";
dataSet.password = "kdevpass";
	$.ajax({
		type	: 'GET', // method
		url		: 'list',
		//url		: 'list?username=kdevkr&password=kdevpass', // GET 요청은 데이터가 URL 파라미터로 포함되어 전송됩니다.
		async	: 'true', // true
		data 	: dataSet, // GET 요청은 지원되지 않습니다.
		processData	: true, // GET 요청은 데이터가 바디에 포함되는 것이 아니기 때문에 URL에 파라미터 형식으로 추가해서 전송해줍니다.
		contentType : 'application/json', // List 컨트롤러는 application/json 형식으로만 처리하기 때문에 컨텐트 타입을 지정해야 합니다.
		//dataType	: [응답 데이터 형식], // 명시하지 않을 경우 자동으로 추측
		success	: function(data, status, xhr){
			console.log("success", data);
		},
		error	: function(xhr, status, error){
			console.log("error", error);
		}
		});
}
```
- Map 컨트롤러로 요청할 경우  
```js
function ajaxMap(){
	var dataSet = new Object();
	dataSet.username = "kdevkr";
	dataSet.password = "kdevpass";
	$.ajax({
		type	: 'PUT', // method
		url		: 'map', // PUT 요청은 데이터가 요청 바디에 포함됩니다.
		async	: 'true', // true
		data 	: JSON.stringify(dataSet),
		contentType : 'application/json',
		//dataType	: [응답 데이터 형식], // 명시하지 않을 경우 자동으로 추측
		success	: function(data, status, xhr){
			console.log("success", data);
		},
		error	: function(xhr, status, error){
			console.log("error", error);
		}
		});
}
```
- Entity 컨트롤러로 요청할 경우  
```js
function ajaxEntity(){
	var dataSet = new Object();
	dataSet.username = "kdevkr";
	dataSet.password = "kdevpass";
	$.ajax({
		type	: 'POST', // method
		url		: 'entity', // POST 요청은 데이터가 요청 바디에 포함됩니다.
		async	: 'true', // true
		data 	: JSON.stringify(dataSet),
		contentType : 'application/json',
		//dataType	: [응답 데이터 형식], // 명시하지 않을 경우 자동으로 추측
		success	: function(data, status, xhr){
			console.log("success", data);
		},
		error	: function(xhr, status, error){
			console.log("error", error);
		}
		});
}
```

## 초보자들이 잘못 사용하거나 접근하는 경우를 알아봅시다.  
- dataType을 지정한 뒤 그 형식으로 응답하지 않는다면 정상적으로 응답해도 에러 콜백이 호출될 가능성이 있습니다. 예를 들어, 스프링 컨트롤러에서는 문자열이나 null을 응답하는데 ajax에서는 json으로 지정할 경우에는 서버에서는 정상적으로 응답되지만 클라이언트에서는 해당 데이터를 json으로 파싱할 수 없기 때문에 에러 콜백이 호출됩니다.  
- 요청 메소드 타입이 GET일 때 processData를 false로 지정할 경우에는 데이터를 url에 직접 포함시켜줘야 합니다.  
