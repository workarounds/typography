#### Add to your project ####
Add this line to your dependencies in `build.gradle`
```
compile 'in.workarounds.typography:typography:0.0.2'
```

#### Using the views ####
There are two important attributes that define a font
+ `font_name` Ex: 'Roboto', 'Helvetica'
+ `font_variant` Ex: 'Bold', 'Condensed'

So if you want to add a TextView with the font 'Roboto-Condensed', you add the following to your layout file
```
<in.workarounds.typography.TextView
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:font_name="Roboto"
    app:font_variant="Condensed"
    android:textSize="16sp"
/>
```

You can utilize the following views to improve the typography in your app:
`in.workarounds.typography.`
+ TextView
+ EditText
+ AutoCompleteTextView ( to be done)

#### Font Files ####
Place your fonts in `assets/fonts/` folder and use the following naming convention
```
`<font_name>-<font_variant>.<ext>`
```

`<ext>` can be `ttf` or `otf`

Note: If you want to use `Roboto.ttf`, do not use the `font_variant` attribute. Although we advise you rename the font as `Roboto-Regular.ttf` and add `font_variant` to be `Regular`

