
# Map Markers

Работает на Yandex MapKit.
**Внимание**: в репозитории отсутствует файл с `const val API_KEY`, в котором должен храниться API-ключ для работы Yandex MapKit'a


## Возможности

 1. Определение местоположения пользователя со следующей за ним камерой или нет
 2. Установка места путем **долгого** нажатия на карту с возможностью задать имя этому месту
 3. Список всех сохраненных мест
 4. Возможность редактировать название каждого места или его удаление из списка через кнопки в списке или прямым нажатием на нужный маркер 

## Интерфейс
Кнопка слева сверху: 
- Открывает/закрывает список (на RecyclerView) сохраненных мест.
Нажатие на место переводит камеру на это место. Также у каждого места есть кнопка редактирования названия места или удаления места.

Кнопки слева снизу:

 - Кнопка, которая поворачивает камеру на север и перпендикулярно земле
 - Кнопка, которая переводит камеру на местоположение пользователя с последующим следованием за ним
 - Кнопка, которая переводит камеру на местоположение пользователя