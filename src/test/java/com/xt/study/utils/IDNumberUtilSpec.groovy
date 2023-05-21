package com.xt.study.utils

import spock.lang.Specification
import spock.lang.Unroll

class IDNumberUtilSpec extends Specification {

    @Unroll
    def "身份证号：#idNO, 年龄: #age"() {
        expect: "执行及结果验证"
            IDNumberUtil.age(idNO) == age
        where: "测试用例覆盖"
            idNO                 || age
            "420684199004233514" || 33
            "420684199104233514" || 32
    }

    @Unroll
    def "身份证号：#idNO, 生日：#dateOfBirth"() {
        expect: "执行及结果验证"
            IDNumberUtil.dateOfBirth(idNO) == dateOfBirth

        where: "测试用例覆盖"
            idNO                 || dateOfBirth
            "420684199004233514" || "1990-04-23"
            "420684199104233514" || "1991-04-23"
    }
}
