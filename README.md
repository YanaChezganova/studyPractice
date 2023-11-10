Skillcinema - это мобильное приложения для поиска фильмов и сериалов, а также управления коллекциями фильмов. 
	
 	Источник данных kinopoiskapiunofficial.tech. Пользователя встречает Onboarding — знакомство с основными разделами, 
  	на Главной вкладке ленты с подборками фильмов (четыре статических и две динамических подборки). 
   
   	При нажатии на элемент списка открывается отдельный экран с подробной информацией о фильме или сериале. 
    Помимо общих свдений есть панель с действиями над фильмом: добавить в «Любимые», добавить в «Хочу посмотреть»,
     «Поделиться» и тд. Также есть ленты с актерами и командой, разработавшей фильм.
      
	Для каждой персоны предусмотрен экран, отражающий фото и общие сведения, лента лучших фильмов и 
 	сериалов с участием персоны.
  
	Нижнее меню предлагает вкладку Поиск по названию фильма либо поиск по заданным критериям (жанр, страна, рейтинг,
 	годы выпуска и тп)
  
	Вкладка Профиль содержит две ленты фильмов (просмотренные и тех, что были интересны), 
	папки с коллекциями фильмов (две предустановленные и пользовательские), 
 	пользователь может создавать свои коллекции.
  
	К приложению подключен у Firebase Crashlytics для сбора информации об ошибках во время работы приложения.
	(В приложении используются Retrofit, Moshi для загрузки данных из сети,   Paging Library для постраничой 
 	загрузки данных, Room в качестве базы данных, библиотека Navigation, Glide для асинхронной загрузки изображений,
  	Dependency injection Hilt. Компоненты ChipGroup, Custom View Group, Recycler View, RangeSlider, 
   	BottomSheetDialogFragment ) 

Репозиторий studyPractice содержит примеры заданий, выполненных мной в процессе обучения.

	1. Приложение SunnyDay реализует API api.weatherstack.com, показывает погоду в городах мира по запросу 
 		(загрузка данных с помощью Retrofit, Gson). 
		Также имеет локальную базу данных для сохранения сведений о погоде в просмотренных городах (Room). 
		Внедрение зависимостей произведено с помощью Hilt.
	
	2. Приложение TimerCustom отображает аналоговые часы, которые показывают текущее состояние таймера. 
		Виджет таймера реализован во ViewGroup функцией onDraw 

	3. Приложение GoodMorning использует в своей работе Work Manager. 
 		Приложение определяет местоположение пользователя (Location Manager), 
		рассчитывает время восхода солнца в заданном месте и устанавливает будильник 
  		на некоторое время после восхода (по установке пользователя).
 
	4. В папке jetpack_compose приложение Rick and Morty выполнено с помощью динамической верстки Jetpack Compose. 
		Оно реализует API rickandmortyapi.com (загрузка данных с помощью Retrofit, Moshi)
		Данные загружаются постранично, с использованием Jetpack Paging.
		Приложение умеет отображать ошибку загрузки, индикацию загрузки.

	5. В папке m11_timer_data_storage приложение использует Shared Preference и локальную переменную для хранения данных.

	6. В папке m19_location приложение Places of interest имеет возможность делать фотографии и сохранять их в папку 
 		на устройстве и базу данных приложения,
		Также приложение открывает Google карту, определяет местоположение пользователя и 
  		загружает (api.opentripmap.com) 
		на карту отметки о достопримечательностях в радиусе 5км.
		Есть возможность раскрыть фрагмент с информацией о выбранной достопримечательности.
		Приложение содержит UI-tests, unit-tests на некоторые элементы и функции.
  
Reading - приложение, созданное мной обучения детей чтению. В нем есть разные уровни сложности, 
слоги для чтения генерируются произвольно в соответствии с уровнем. Уровни созданы в соответствии с логопедическим 
букварем Е.Косиновой. В процессе чтения появляются мотивирующие картинки. Приложение одообрено моими детьми :)
