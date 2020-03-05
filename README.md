# PULsE

[![Build Status](https://travis-ci.com/kotik-coder/PULsE.svg?branch=releases)](https://travis-ci.com/kotik-coder/PULsE) 
[![Code Quality](https://www.code-inspector.com/project/4377/score/svg)](https://frontend.code-inspector.com/public/project/4377/PULsE/dashboard)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1c354f5fe4d0435d8935f11cf2368d8e)](https://app.codacy.com/manual/kotik-coder/PULsE?utm_source=github.com&utm_medium=referral&utm_content=kotik-coder/PULsE&utm_campaign=Badge_Grade_Dashboard)
[![Maintainability](https://api.codeclimate.com/v1/badges/bbbb695c6ffa3fbcb7e9/maintainability)](https://codeclimate.com/github/kotik-coder/PULsE/maintainability)

## General Information

<b>PULsE</b> (<b>P</b>rocessing <b>U</b>nit for <b>L</b>aser fla<b>s</b>h <b>E</b>xperiments) is an advanced analysis program for processing data from laser flash experiments, allowing effective treatment of difficult cases where conditions may not be ideal for simpler analysis. PULsE analyses the heating curves, calculates and outputs the thermal properties of the sample, such as the thermal diffusivity, based on the inverse solution of a heat transfer problem. The software is specifically tailored for use in the Materials Research Facility at UKAEA, and reads standard ASCII files generated by the Linseis LFA systems; it was initially designed to read custom file formats from a different apparatus designed at the Moscow Engineering and Physics Institute. It is therefore intended for use with any LFA instruments as an alternative to the standard, often simplistic software.

<img src="https://kotik-coder.github.io/Screenshot.png" width=100% height=100% title="Loading and executings tasks in PULsE">

## Main contributors and license information

* Lead developer: Dr. Artem Lunev;
* Beta testing, validation studies and User Manual by Rob Heymer;
* Thermal transfer models by: Dr. Artem Lunev and Dr. Teymur Aliev.

Licensed under the Apache 2.0 Permissive License.

## Feature requests and collaboration

Please do contact me via [email](artem.lunev@ukaea.uk) in case if you are interested in new features, such as new input file formats, new heat transfer models, or anything related to the modification of the GUI or internal code structures. Anyone wanting to contribute to the code development are cordially invited to make thoughtful commits to the repository! Those will be reviewed promptly.  

## Build instructions

* The first step is to clone the project files to your filesystem. Change the current working directory to the location where you want the cloned directory to be made. Type `$ git clone https://github.com/kotik-coder/PULsE` in Terminal to populate your current directory with the project sources files;
* Staying in the same directory, you should see the `pom.xml` file. This is used by Maven to build the binaries. You will need to have [Maven](https://maven.apache.org/install.html) installed in order to proceed, as well as the [Java Development Kit 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html). After you have installed both, type `mvn install` in the directory where you've cloned the PULsE source files to. The dependencies are managed automatically with the info contained in the `pom.xml` file, so you don't need to do anything else;
* This will produce a `/target/` subdirectory with the `.jar` file and the `/target/lib/` folder. These can be copied to wherever location you prefer, but please remember to always put them in the same directory together;
* Allow the `.jar` file to be executable (e.g. on Linux) and double-click in order to run. If nothing happens, type `java -jar <NAME_OF_JAR>.jar`, replacing the <NAME_OF_JAR> with the appropriate file name. This should start the PULsE graphical interface. 

## Eclipse integration

If you want to use an IDE for modifying the cloned code, please consider using Eclipse. It is quite straightforward to import the project to Eclipse. Once you've cloned the git repo, open the associated project with File -> 'Open Projects from File System...', type in or select the project source, and click OK. This should be sufficient to get you started.

## Pre-compiled releases

Please visit this [page](https://github.com/kotik-coder/PULsE/releases) for a list of pre-compiled releases.

## Standard usage

Click [<b>here</b>](https://www.draw.io/?lightbox=1&highlight=0000ff&edit=_blank&layers=1&nav=1#R7V1bd5tItv41frQXVdwfO3EnnZlOr8xy52R6XmZhCVkkCNSAYnt%2B%2FSkQhWBXAcWlSsi28xAJQQlR%2B%2FLt%2B5X%2Bfvf0MfH228%2Fx2g%2BvsLZ%2ButJvrzDWXQeT%2F%2FIjz8cjyNbM45GHJFiXx04H7oL%2F%2BeVBrTx6CNZ%2B2jgxi%2BMwC%2FbNg6s4ivxV1jjmJUn82DxtE4fNb917Dz5z4G7lhezRb8E62x6POtg%2BHf%2FNDx629JuR5R4%2F2Xn05PKXpFtvHT%2FWDum%2FXunvkzjOjq92T%2B%2F9MH969Ll8%2B%2FT8Lfz9h%2FXxH%2F9K%2F%2Fa%2Bvvvnn3%2F83%2FVxsQ9DLql%2BQuJH2eilf8T25%2F1vGP3rv8n3b%2F4P%2F4%2B%2F%2F%2F5aXqL99MJD%2Bby%2Bpn5CjiSHKCX%2F3Xz3kvLXZ8%2F0kZJ1ye6RN%2B8et0Hm3%2B29Vf7JI6Egcmyb7ULyDpGXmyAM38dhnBTX6Zbmucgmx9MsiX%2F4tU%2FwrW1pWn5FHGW145viL7%2BCPvt8WcHHUT62n36S%2BU81Yigfz0c%2F3vlZ8kxOoZ8a5eMoaV0v3z6e6AZRYtjWaMYpj3klqT5UK5%2B2g7wod2TA7jjM7lxhKyTf%2Bm4d%2FCQvH%2FKX%2F%2F78%2B5F9NsFDziAB2ab8Lo8n3if0PHqE3Entas6CXrQmC%2FhPmZ9EXhik%2FnraemS3g%2Bghp6Yw9tb5avwLAZWRLd%2FnL8k%2Be2Hoh%2FFD4u3IiXs%2FCcjj9RP42ZfTBwMJc%2B35zmbFI0xr5fj3OQEm8SHK7%2F14vRJyxJrVIEdkcejRNFh6tGTRIzY4BAmFQ7T%2BJRfb5N0q9NI0WDUfvf8UZP8mr7Ubs3z3V%2B2T2%2Fy3a%2FTNM30TkbuvXZS%2F%2Fav%2B2emy4h29btjOpPEhWfn9wjLzkgc%2F6ziPyg1%2F3VBN7EbXNtLkyBV6LPFDLwt%2BNhUab3PLb%2FgSB%2BT3VnTkNKVapc%2FoCsffXV5UVyBgHYSaCyFIZ8cHwyxEyMF7rp22z09I2%2B8Xa%2BB7dG3YfTXPJy%2BOd3Ci%2B2oLJrCCeXZWsLQGM1xrN5rh9nBE8a4mKM%2FJJs7L5BIdUKO9DC7BrnsGLuEhmHYuieIoR5VrL91WqrbOL5T2UUMJnHRCG9FTPqtx2Ynn%2BHw2M08si9YrFEuJ3RxL7DYgQleM2GejL%2Fc8Unhm6lgYYIAKdTbEwCBTyeRBn2uNPD5%2B%2FVRYS7s9kTWFgNXILuaHEt%2FLcuMEkM8J%2BGsy7IqHnCTLxcld5cSpzsIwWwBdn8ErzcDQeQYGY1CWLorUD%2F1VsYMTjNMsLu3SCWsk3iNZZO1l3pX%2BQdC6Tbbx7v6QDiapzca3VlySWtvufeFDUUI6Rgtn10nH4JCONF%2BJzkKNv%2FyU1QZExN6Vb%2BMk28YPceSFv56OvmsyfG0nvvtZ9lw6OL0DIRz93WmF3%2BN4T3csjrLyNHREKpUCug%2Fj1Y%2FjoQ9B%2FvtKtEL9pk61sdRX2ZAF%2Bcmhd%2B%2BH77zVj4fiRikBlNCJKjOqvkSgzsmkGAar5laCJe%2F32g0mEtSWJakSw0h3dYB%2BJ2pKekq82aR%2BtxJ0AdgyjJHatOKxyj2pVpsarMn7RzyZwbgsxGG1GiOm5IdmkKWKg02mAmYFYDou7NM6OWVmijd0QYqnrCFK8dqNZtMwxlQsCDCCDgV4L%2FW2s8pshKmLoIacVrX4kO0P%2BV0FOW7YJ%2FH6sBL2SI%2BNeyxFZzPxDUcQ70lT2lSUd%2B%2FcbY6s8tBfsvMy8TDGnmzvPgkIqq%2FBsn5498KAGkb6wpCaiV%2BUHmmJTVQ%2Bqhb01ILj6kHPc6mbEQBLI7BmFn2jY%2FfGMt3Tn90ETxDziIInHbhldYjCWsCTSj1mstbvazJh6qR%2FwXYM5Z5%2BNjMHsxlyJ3LZALvFAAEG7BLOtMZxnwGjK5y1JFsvJmu9%2FOmlP3LvT5BWiLDyAr7Pj0THFAjy8tPtyVOY%2B5AfopldhRtn5fPhwr1jGqZ2blehAX2FCl2F4Qfr25fs%2FW%2FI2Xz44dn37n8%2F4WsWOZ4HRAiIxmWIJYsVS9wHiwWl0kRFfw2MEZPmFaqSBxZDQOpczVOWIT%2FcK5K8LsCoqYup2SUThYSWyYoipQYNlXHdpPSFWKPkRz3TEydSU6G%2BtYKm1v7GO4StlvGcWmohRu01cIUihO0bDhkgDhnAWPd8ZMDatZx9%2B0zYd310aHSnQzJX5vdcpGa%2BOUJG0UzTD%2BKYXIqxOBSDoNE5H8kIZfjWSOaQDiCYQjjs4%2F0hzLe%2BcHdCCTSWFl6CDEF6kyLINnNJQmkEnLrtX2mKLbXS%2BpMHRWGqmpQZg944dS%2BNzagy7eZCWHFGlS2KZe5Df1eQJZEtu3zzRaUSufEgKOsKzqGOzPwfT%2FxYxd8VU4Ry%2FFNmaENHJRLUUtLgrc26JUvPSRRnBev8DPzHcjfn2qV%2Bd4ia3cDWjdN0QVeJt40NsW5oRriayiAWa94VNmoOD4OkcGhlxSap9VGdxxeFBZ1R0Oc%2F33bYzHa8Js89SD66QNxBZVwv7qD6sRd3VNJiGr4Y4LC3MMAgg1M1WhYyYP7v4BKGuVGKy2Z8zOD9vRR24wXKLpHnRAuFELYEma4WkHa1iQlQAzjPdADDGO6NW%2F8bx4cmai5rKU74c1mtxvLYy7VFKcbq1wmi6XlqbFFIjYyzQpj%2BwEKqE06RJuRR%2FSXKn%2B0EV3rpFwumuuQjvyiVL9xu9%2F4Qv24uYF5N6cA1QrhBVzbCXOe9w7II9IbMhuCRxuYHvCYI%2F0IwRSUyeoU2jdYMwRSmgZVhimur6Z22rco5PVSOw6UcjVlKuiSf1a2NL44udVGsa4pCXUWloD31vaIkCPK%2FdFeM%2FoYWxcM0M7OnJn7S6UZPxb3debqcgnuEhPKiXoCO6tXDF6zFqG7qt4xHaDF1KgwZTc%2B5a%2BHRKszVwVKiKkydU6pyU%2FTGznb7wk9PiHqq3VJG4SZ15%2BoJ4AHR8erbcsHoQ%2FW%2BL%2FogLfEI6aydfLeN86J076cXEGlatIRbB8QCJE9jlb9JV1uy66x9Myg%2B1B0OuLTAq2HcgB45Fi%2BwpBNDALG7Ky0jBOmsZXrLbmWnFGiE34PsLfwuHH63eSSgNPyOdFatvKJgx%2BXHFisG7odzuj0MztXjHJURMw3fPXMvEKkXAjm57tgUKLgQ0hSXBlTboArIcTTzGxoTTdBpEovDK292OPJaWi4I4nSiYaX1yw2bVWhUQNyJVkAqcnaBJE6kjY2cYc280ep%2FMNMYgYVlSzRK7ZNocmQh2wIa7NE8%2B36SpPGDxZBk04Vi26OjAFgDS%2BHpLpQWZ40NyB1VAYdW%2Fw4ypl%2FjOIMvsU1wiSS3LA0cvUUXR3VjXYhqE%2FbLWqL9Xk9uWRMrrO2%2FNnAjQcluigYHjXbUOlbnwsJu2%2Fk4j9ccql3zleYmV%2B0Na%2FAyjPb6SWpZPWJZga2586mmGbz7wnrGHqGb3N5rnJ4rZOkZ1vb4VDn8rooSwGPIALrzppQFLN2lBxz2puhcF3kmIrfv2SsyEWmkrB%2BPG8vqnK4DE9Ec7eTSwEKqcys5XdhGk%2BCoLk0Dm%2FjPTYKGqEm4sOzes1PgUJVrgYiK2ZNlA8%2BnYliu4jSHwcQeB8kw6boAB4k4O1TuvYXwAyySsMe2V0YW9P7BkN58Ihn98vE%2FwVfnH9%2B%2FR093767te9td8zqUsSSotKHVicpPLoHpJhEgnDmpmNc%2BgP%2BsLT7NThXOIOfPVTzwAnFoqIH%2FuZGmV2wLuMb5bYHXXWVVBVv7NY9ww0xFSExvKgwXBnWEe9OCOitHudQQnJ3R6SY7%2F5At8Xjiye2KDDpDShHRgK6q7tiMepjrbgmGFIfCdwOUANhWN3yH51v2wPNRB9xX2Y0ZifVzKztEPnrHYVJF8d%2Bktn5lF49mh67e63ZedPDC8Pk1d3jDDUJqa%2BfFY255ZYWWUKeCFxDV66%2FHuGDrWDzwJxqlqVUVUgCkIO4HwnHExB0ds3FRV6DPZReeSR%2FBn4BQt365NjvPl%2BROsoRKQUrdEUTFTBrRaXP%2B34cgObWBHCzmZeYMDqoIWGZGIRxthzSdW56ObVaPyLMSuX35BlqJo%2BcuYNHsjKZLH6tucS6cZ20vy5KESayjp5xYcGaDYOem2eSezfNhyiyB88j20L7IE9YJg8j3koLIQiIqWnH3mygdIkptQNcWK0V1DlfJK6Vy1ErRS3TH2cJC1BEdE6UoEAQ75%2BojhagNnPiqW%2FBWD7ZbiDb8%2BpM8Dl6yyoVos%2FBEbk%2FwQcLNR2vTt3nCzbVs3cuDCnTQBONrqA%2FNkSbmTumSp%2BxlVtTR%2Balz1wTzx%2BO8zccZKPgcTgtQ%2FqPFgnJvokBz9WYXwWZKIWNrSJdKhohUkuUOraTUpMWyLIge0nP4SAVkmBJIZrlAufG6WbscLS2vvtkRyXt43aCMIgIBULasbDUbdmQaW1EHF1Lei9LhdTDjgLLjeOkJUurNvyfbKAVdTTHPuafULHWFVOtdMfGrGvY1jcbS4xrdo6SUIXwioXFLBNG6t0xl2tEFuaeGaKmAvDGXbHbQQnH83DqP9uLp13muqM4TVmYT2VlIVcyMlHfxOtgcf2TaMQyQua6UAGdJG1gG00OFUHnjz9byB2tC4cO%2F2vND2uEzZV4V4Jlv5p8XSguLlaoocSlQ2uzJEhSH0mAhxVC6erBvwfHLA8%2Bw%2Bog3fFfjMIG0yHjVoli2%2B%2BBy05cwEi3uwchdlsyDomp0TMeBLnpXcWgcU7exeJK1SlU9krilWgA8j3y3c2YhZGuBmjTDHkm2ltUCTZURLVsZ8H7rr%2FLhj4mXN5gt5xLf59ToJccRKsesjlUcpUGa%2BdHqmSFyeZ6Etek7a4OnNh18r1uMUSF1Qn2VKgnSIKzzzxmuJH1tZ3%2BN0kOSR5PD%2BLHAXsWukv8fEt%2FLiiPZ1ovyZ3QIi7BzFkytH5O6uao21OY1HVS7nVgRCrrcIEpF8f0oCOuLUicmcFA4YwvNIJxiWrSevYd%2B9eh7bMP9vi6gRjvNCr9b7ngL8x8uYQTXZYo3zQBg2bE5Ak7XbjgZLRDqzCji2CjJG4DulHiUmQQk3rISomEunzO2yYnNNHWA9LkAkSeUM%2F1LMXH9cHSKTQ0WZMkhWhFIVwH4F%2BcbUyQoYakIL5vGUOoO01kg%2BCfc7SUBdll7gzSQfI4Q7cLc56vUzembww3mUkG4%2FGDuQrp0YTr7ol%2BDWaLZ6FN9Ow6%2BoYUMFWVhTbDacTalwRkKwiu08ZNdQAy9OJqmL4gwjzJFA2MuEjI7mKUKjXolG%2FLmNGSo4RjQpemDGdrLLAw0zw6TRUteKq5bCEw20Eww2bDGhYSHFm%2B7NgRMPd1BHLsFv0st38acdu0cqfjZz7zS%2F51mcRF1Ln3fm0N29KQeUvE54I9BUcvoPxHATJQ5efnpNh0vbyUm6y2j3Qdo5VWbZ3A%2BN6wx2EfBdlJaWA99qS4KKg9mHMxx6qxhYtrVUtGwDlBbYY8Vx7AFgS1JHMPeS7S3rGivJhNP6u0k3NOqvbeH3nX%2B1dl6R2GD9enwLL25XJWjGjAPrLeTG%2Bun7N2PwQxRQaCG6TGYcDF%2BCgKclWGonpVR2dBKE5dXWy96yLFSPYdAPH5zBFy1ZIQ3G7UNLzFzFR2Nh5iUVv9hTpc9mZLy4hHTgKCOsEvsBJkIZlEKmWA5KtJGZ%2FgBzOQKdu8eipkc4FZ2tW7MBM%2BvYgStOV%2FMFEJtKajGGtwn9rXDGuHGgVXHuIXAGtinFmljjRndbGGAs4Rg%2BXEZEdjzJYlXfuHjyR018YCihGybw5td5S1S4dFZqM8Go6bFZtgKXTbcvR8294Mjz15MuogpPJRpaUNSgeo3x1ZGQQeOKamnNvCCz%2BrmmEsmDhtiXVo%2Bay%2FdVgVJM44OHgIEZuaKyt9waYOiTAA8jbHI2gTkqktC1jrw%2FtnlZIz2sQngvvQebyRw1YDz5bARp%2BrnturgUDU1%2BuklgXcfFtUTx16WVa%2BiIoa0ngYGGqp9eGMiWWDABcgQ87qmqe22MEMrIpXGy9wIQLj3I81hWIisY9oMjZ2qYQOzx5Ak6yxww0a36GJOt1SIrmEQYAAz2C%2BLG6oquaVwg96jsEc33YIQYi5ugBN6u11q8HSrbI0olxvYernhvq8X49gakJhJaWYhzAHRrDu61zrA10zy1XzROr7Tiue1Gime5%2FBdiA1%2FnJECRVNHxOMgimIeTBwOVnULy2eQX%2BFOLm5p8YtoMG8PDw1i4K4gxmwsIVY8%2BNZYZnmNZZipK0jjBqdlTV3h05PqisMxsGBghYZcWEBFbW8T7YWVWFeBukomW3hsHg9BBjfEjqz%2B9N6Vzx8Am9HvMQ5LqLL0Oomxd2z0sogWenjHJ0%2FAuUGIGUwwl48XcgP9Irm4YI4Ry2Pl7QJCGDyofAH0jTSYHYTgIsIE7igicIQguqf33Bp0ZK6Ymye4W60JsEQYBvvU78efXrrPpzTot5vgKYeNnQEH4ZpgBjyKskQryERMLopJ6aJG0Q6HoufIf%2BRug5BoUloQfJJ%2BJ6jYjOGOqdYDImWcdOuSWb2y7Tjnm6WNqda8od3UwJ4G7F6Jcwq6nkaNnO7oCJVNkKRZ8aTyVhKAxqYlF61W%2FOQi3dJdfS2Ro01GrvOaN8uKIXJ3QNBPvIAif9mcaQtypjORMacJYFYRLr0nwyhj6fzbfDQyW1n5Wrtxdar9pgvlpliwBGf4DY5cMV%2FUFZmVlKzdtSu9Oa33ob8raN3L%2FJ1f3NyEYp78x8zfRY0dHLBxnPPoG2iRmLwJIbymMtIQJGfS32tVOJw21%2FxHpp1V4%2BA3jaNmnwU0jkYT9CZqHAf06JSmcdgvWorG4aULMQriNthsfEIGK%2FG%2BGEPn0r46pQQHFvOVElKqlNiAwlKV0lKFHN2vfm1mTdRmbX5JaFubME9Bsi%2BDE359U5RyaKhfUzqzqMlTiEq2nuR801IUJXdiDWubeUU7qDQOf%2Bb5Kq36i7kwDCLfS2qXjlKTKsfAnUtxujDywlWcvP6t0hSnLhKJX3Q8QCwPUKLM42Smck80plqB4%2BQSk%2Blk9uTSOaBuCV4gJz5IH2O9j3AROCDPx1tPHOkCRICmOd6xBhmKAE0z7eKTTRxlvNQpWaKBg34MjmxQGlngZOmUWxJEq3i3D%2F1s4jQWWBtmEuHH3Zd3OI91LWJfLJUzevky%2B80D1wyyCghfe6LsnbZhb4aFqn2eR8myme%2FQDy9xMlrnA%2BjG8KV4jo59Q6Z2SrvP24is8vFrilpxLzjOr3MSd7hSXxpS5%2FQIXqrUXypUN0TdE8e2urNLEffcUsS8vIEQC8v%2BEiahFj10cnDp1dChqVRl2eOoSqUPymB9UJ%2FjpJjpSHRWWqiXy1cbNtyKBagNFntWGXqR%2F%2FTSEvSYHcC8UXJKzTVOb2ZW5IoWu1wNzeqXKApblOR5WnCccq0H5%2BpDBxmzkqQmHFWyrnAVga4iU5%2FTIfcNaA5jDV5rO%2F6z1vk8NJU5QH2V6HyV2UiItVYXjzObDg%2F73B4P4e6IZTeY2WnIMcCMHsHWRLPZKmxAoIEYsRb6m0wxcOTUps%2BPYZhOH7xIgKUSRFqXVzU0apCgPHa2OGVDHV0dZ%2BdmSFOmpBEvFsiCsHsGbsHzQWcySQino2rpBdpEEODaZ48s2sO6ILMzq8ZXOUuMUbTkNKmxhxjfojm2OL89Y2D2nCm9J2dK4IqJyQnkbRLHWf10wsnbz%2FHaz8%2F4fw%3D%3D) to see the complete flow chart for setting up the calculations: what user input is required and where, which parts of the calculation can be modified, how this affects the state of the program, etc. [<b>Another flow chart</b>](https://www.draw.io/?lightbox=1&highlight=0000ff&edit=_blank&layers=1&nav=1#R7V1bd5s4Hv80eUwP4s7jNGna6W5nu5PtzPQRG9lmg5HLxXHm048ECIMk27IBgdPknJkagTFIv%2F%2F9ohvjbr37mPib1RcUwOhG14LdjXF%2Fo%2BumaRj4HzLyUo4Az%2FTKkWUSBtXYfuAx%2FBtWg1o1mocBTFsXZghFWbhpD85RHMN51hrzkwQ9ty9boKj9qxt%2FCbmBx7kf8aN%2FhkG2Kkdd3dmPf4LhckV%2FGdjV%2B619enH1JunKD9BzY8j4cGPcJQhl5af17g5GZPbovJTfezhwtn6wBMaZzBcW%2BdPi%2Fg%2F909%2FJn%2Fqj9XVuGx%2Fz2%2Bo1tn6UVy9cPWz2QmcABnhCqkOUZCu0RLEffdiPvk9QHgeQ%2FIyGj%2FbX%2FBuhDR4EePD%2FMMteqtX18wzhoVW2jqqz%2BA2Sl7%2FI999Z9PB7dbvi4H7XOnqpjvgpqGYlRXkyh0feu3rNzE%2BWMDtynVteR%2Bag8QPVBH%2BEaA3x8%2BALEhj5Wbhtg8avsLesr6u%2F%2BhWF%2BJF1bUfRXoGEkomrtW9RPmj1rf0i4w%2BNx9gPFUt%2FBgx0CRhEEaY5stzPqzCDjxu%2FmOBnTPftxfTTTUmIi3BHQPF%2BEUbRHYpQUtzICCzoBiYeT7MEPcHGGVefGbaNzywTPwjxmt6HCb5ViGLyQzDNyJcoFYFj67%2BFSQZ3R1esOuu0J54S63OD1quhVYPM2eVprnBrbc5dCOBxK%2FEds79RaDL4hfBPfDiL0PypHHoIyfvsSfGvJgV%2Fb1Nw70Rr8UT74cMfX%2FPPT8m33z5vk%2B2%2Fkpdfv7j0usGJ1mxjxwBqiRYYPNn%2BhqaJlV2Y1VDBnxtIwUd7oJCDFxZf2o0SkSBAl3jaLU8SXxVOtHeGQfWgjpAzXKsFOdOQgxxeHf%2BlcdmGXJDK%2F47hMrrFqecyW9fjD%2BUTMN%2Bmj4MWixQOQiOWQLDZEREkQbjFH5fk4y%2FxS7Hw6ROeEo1eMEvoeTqCn6DxLcGNwpjcZwXx%2F3%2FkkPzig%2FzdGLJNVmg9y9PT8pYRr4sFtOdzkXgNHG%2BmaQPIUB0waDF5KQoslWJUvx7OeKHqSznqnot%2Bb%2FBXMUe9nDcCV5I56oYi2QvastfS5BghdyNG%2F%2BMYajk1wyne7pu%2B1wPqbDWou8WyXGv86dQNwIhJZRogj55HGGGTCQOhFGjFO%2FvE%2B%2FJwzRLmFuiMri0SMbpSS0177SLmcsKVpVugyL1yaxynW11Sje6Nbnn1ZPJcX1PF9aXBo0jVqMExElh4Hi%2BwPN772XzVzXiBOzjPS2fbT2G12IzL1RNIFE2pRBEZq6OyhHnkp2k4bzGFcfz1lNhPcgVFvj%2FdO6CPKPP9cVi5w4peRlwPPv7vASVPn%2FGPf0WI3GuBqYl6Jiq1sHRQcPTbQs%2FkaZjjzZqkWmgP5nngF%2BY%2BTDdR4X5LMz%2FLiY9pDdOUBB71O4LV3TzKA7I0iwStiwHKia98eawDRvp4q2NKsFgR%2F2vyx06%2B7DbDBIMxTL1vhtlYM5Erj4519esw%2FhjOoj7gj%2BEZNGswDuQpZwWBZRzxfPfF%2FY2r0RQ6ANiWBLBh9i3yu7EYm1ubx3y2DrO96C2lcYPLk6co5fYdWm8imPmzCD7kWZ5cvYS%2BNVhCtARCwFApBIypJL0MSD2UKE5TT%2B%2Bu025rwwvoR%2FwOhHgitFyG8ZKhHLiFFX%2FujUokElQGpxLTHZ1K%2BEyUiVKJemvUkJVN1Coc3MHJxN8dR605avBCT%2BBhopRM6K2kZBIwl3RP1fbrLMdiLJH1R105H7BFJpNSPgAEFu1EGUEHgpa1lgB9%2BanIS35xPsIYJqUnKIFpHvUsHWcaNKAtogoNuprrDuGpZbw8zuh%2BBFPnpv0tgMMKvZPE5CmSjoyJ7irO07QAB5a3SPERrAhzensP9nWjfz74L1BZfk1rpSWA29CvTBa0uOkUqKMKkAYjuC6tH2wcPRdD2cqPu93dL%2B6CJceqKJVRHwocQ%2B3SmXQyR1AHIHR7DhYMtHjB%2FiZh6smRlTBW777BA25rRm0Hmm6plTG8AfYmY85Fi6PKWufQ4ilGi5T4%2BuTv5VeeFuIlgT9ymBbphZLiJIPJOoz9sfJKpiBMPNMaWZiYP4HH25Q14Y2JqZK8oP9fGSYKUNxv%2BEfTXL8M8nCmu2Y5xZkFirNWwIj8DUAkhjk1k97iIw%2FVOlAWRrhej6sBLWCKV%2BO9TpJUFa4Gq754Fs%2Bx1K4G0H%2BCELctq5gAfVoxbpvXH76lsJiFbFW8ebQtzVGsg2pzP5rnUemRLLWJGO6IOlTU%2FJXm8NZPQhL0vv5InskUwwHNHZ%2BYROGZV0ZMNY2cpCZbnxQxAUEN2ERXR72lL7%2BqQJcty%2B5svbEUDly15hsQpjFypte3TVAy3Q7ewBXEU1WEajvcZJ4neLYvNv2uXQAAb2wB4Irs%2FYlzGFWJslQanG4FBFTxF50BkAHMd17jz1XKbegENUsNOI0uzddUlUt%2F5HgZ0jK3fY4tmDDOi%2BT2OuuDBjBeG6HroxO6wwuGqbuBlVWTO7LWlq3KDeyy%2BNHU6hEObxZMNcbUfdmVh4AAULyavJBvBKCr1PPMfyp5cIaIaR4hfNBja4ApuNuB5oztb3d4fzv1JYZriPKsV9k3YUfiBJwfLr8WU2Vyl5rX4l5AdRFaz72AqB48neRn3WkLUtkEr3NrvW4tg0mzthUUe7l8Iv7UdboL8XtORaUC%2FFqq8GuwBl%2Fdi6BvABtMKqRXsedDVZSnvjAQ4vmEyRPpCXVKXJe0Ogjjykwt2id1cXiVVXwwemnkPLyiDAeby3CQbPjH4ro3Ke%2FxPvRXxiQvZ3ierOHrKfKgG0zrFdUJ2Z7IHcoR8T2CewZDz4f07O8Nsq4Hb0vfV3BOUVMKIXGd%2BcVjJyWpPJzFfq6XkbBdHOuytSYfcVRabh6vbFWWm7%2Behcsc5f1GqCdku3EpOaISVLVxa40nVKHtxvNc4eYCw1tVfTmtpFm2KZvMpqZ9B1Oo5Up2U%2BXu4x6AYs%2F6cB2Zo7%2BjonkH0ETq7WFIH21Ccx44xS53Z8BSMNmkTOm%2BwWpwzHUuYnmcdBuaA%2B1h%2BkayyWwgYpkK7DRw%2FT1QT8WV1brWgKZL0gsAiqLRFqMVyDba7o9b6gJuKVDWa1W9LnruYsFHCBWdesIYjlG2MEKRtWO3e%2BKaghI4YKvUxYGgyeHEmYsqkx5o0l1ygaISOJfR2ZT3QtekGuX%2BZ1N4%2BeBug0gvEkl2EIR%2BhJYXswFMqxvyES81tvMhvlXir%2FGFG5iE%2BL1hwp77uj9xrhXvQ3chbPFlz104W7QIAgzAR2xWExnfjARS%2FuTf4TaEzzf1RjCy9dJxUE6ZXyxYmfck%2BV0sqqqWOI2WVm8YO4kxS6btq20pxRifefXfPOQD%2FdLb6DFzbmu%2BBxzRnOv3jq3UUeS4bU3BEnnt1O7FAGQKHPZiPCaVdayMl3HcTMQ8talmxOL%2FXPOUdcAClkB6Mk%2FZBwYqAud1r6YTGkGebfKiOyfqZjMUXTsnyb%2Fb3Ph8bj50Q6faTzhiiZSo3SFvZtatvPssjNDCxU2dodfhdmm9988b4rgsQQZy4qoMEc8eDnOCZqe81JpEWcbBFZDYuEE23ab29EylLs%2BQMhv2LGGTUAuiAw0vyT7q3W7R3Oyhdoa9cQTWhmD8FaZIBon8XQPyA5lCUE6LDfx0Vc%2BreFN15yKft5p0PBpxv7YQkcngh0tikQ4RMbEme6Bg56EHPvRc7PWWd%2Fx6djts5vqBdHxhJ77LaUYp9KW7k40T9Kyb4J2LaA5pkjGjrohWkpwKzPPC95NCHC1MmSjiXIcpmboUcR57o4EQVxd5DQu5n6AZpFo6AJas1lFbAUPHz0ymGtS8NOXEsk%2FcaOhAnPXqc7EVZ4XIg1VVVoipW60NdU0Gcpfm%2FZlMJUy9WVDffJwRPLqSxD9Bi7kT6axn5P6Bo%2BhT33X1jFwmTXYbaEWWHVPEal1q2ZnMZnaynPhcNLsmE5AzjltqNlN%2FoWTPupqLndgjJl8swnkIi%2Bft4ASL8%2FWsqAgrepL0khO2rDbWCIYv6hoh%2F8tgwaoy%2F0vY8n8a3nEhazrJEo%2FtYSBZeKrA733sKZvtfiLoE1Iap2%2FPASALluBw9AcwCoor2oRwqICjcJan0ZGxR3BbstLeHRPcfFfZxyJoelM3JS3DsD1CXGLTzR4gzii0wl02lQK831jGBSrvZdg%2BlkkyWroPW4R%2FqUIK2LKWgfoMHHpgafddz6EJwzh6%2FbkKLz5MEMqal5Oo7RcUQHLFPw%3D%3D) displays the process of submitting tasks for execution, collecting results, and making decisions based on the output.

## More info

Please refer to the software [GitHub Page](https://kotik-coder.github.io/) for a [quickstart guide](https://kotik-coder.github.io/PULsE_Quickstart_Guide.pdf), input file examples and download links.

## Buy me a coffee

I really enjoyed working on this open source project. In my opinion, this type of distribution fits very well with the spirit of science. PULsE will always be free to download and use. If you feel that this project deserves your support, please consider buying me a coffee by following the link below: 

<a href="https://www.buymeacoffee.com/kotik" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-green.png" alt="Buy Me A Coffee" style="height: 51px !important;width: 217px !important;" width=40% height=40%></a>

## Special Thanks

to Thomas Stainer ([@thomasms](https://github.com/thomasms)) who has provided many useful advices on improvement of the code structure and on continuous integration and development aspects.

