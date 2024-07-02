하는 법  test ... /common/infrastructre/CralerTest 실행하기

```
   @Test
    fun `업데이트하기`() {
        val driver = ChromeDriver()
        val companies = companyRepository.findAll()
        for (svcCompany in companies) {
            updateBlog(driver, svcCompany)
        }
    }

````
테스트 실행 !!

resource에 docker-compose.yml 존재 ddl-auto create로 테이블 날리는 것 주의 하기전에 DB에 insert query로 자신에게 할당된 병원이름, 도로명 주소 저장 !! (나머지는 nullable field)
