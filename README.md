# Finder
Android приложение для инвентаризации

Для работы требуется Firebase и его конфиг файл google-services.json в папке app.


## Главный экран
![Главный экран](https://github.com/headneZzz/images/raw/main/finder/1.png)

После авторизации в системе мы попадаем на главный экран. Во вкладке "Инвентаризация" представлен список всех кабинетов в базе. У них есть 3 статуса, которые визуально представлены с помощью иконок: 
* Кабинет еще не проверялся (Желтый круг)
* Кабинет проверен, но не все предметы были найдены (Красный крест)
* Кабинет проверен и все найдено (Зеленая галочка)


## Экран кабинета
![Экран кабинета](https://github.com/headneZzz/images/raw/main/finder/2.png)

Перейдя в кабинет по умолчанию там будет список предметов, которые должны быть согласно базе. Список можно сортировать через поиск и голосовой ввод. Найденый предмет можно добавить несколькими способами:
* По QR-коду через кнопку "QR-камера". Это приоритетный способ, на всех предметах должен быть наклеен QR код, в котором закодирован id предмета.
* Если предмет есть в списке, его можно отметить долгим нажатием.
* Если предмета нет в списке, то можно его добавить из списка всех предметов через кнопку "Добавить".
Чтобы сохранить изменения и выделенные найденные предметы надо нажать на кнопку "Сохранить".

#### Добавление через QR код

![Добавление через QR код](https://github.com/headneZzz/images/raw/main/finder/3a.jpg)

![Добавление через QR код](https://github.com/headneZzz/images/raw/main/finder/3b.jpg)

#### Добавление через список

![Добавление через список](https://github.com/headneZzz/images/raw/main/finder/3.png)

## Экран всех предметов

![Список всех предметов](https://github.com/headneZzz/images/raw/main/finder/4.png)

Тут отображены все предметы в базе. Кликнув по любому из них, можно посмотреть и отредактировать информацию.

![Инфо о предмете](https://github.com/headneZzz/images/raw/main/finder/5.png)
