# oreluniver-api
Неофициальный API ОГУ им. И.С. Тургенева

[Swagger](https://siper.github.io/oreluniver-api/)

## Отличия от оригинала
* Четкая структура, выделены три роута ```teacher```, ```group```, ```classroom``` для преподавателей, групп (студентов) и аудиторий соответсвенно
* Для каждого типа реализовано получение расписания и поиск
* В расписании удалены лишние данные, переменные приведены к единому виду, исправлены ошибки и опечатки
* Получение расписания на неделю происходит по ```id```, номеру недели и году
* Версионность для будущего расширения
* В расписании вместо номера занятия отображается дата начала и конца занятия в формате ISO-8601
* Для поиска реализована пагинация

## Лицензия

```
Copyright (c) 2021 Kirill Zhukov

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.```
