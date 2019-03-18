# ItemDecorationPlus

### 效果预览

<img src="https://github.com/downtail/ItemDecorationPlus/blob/master/screenshots/masked.gif" alt="image"
width="160" height="320"/>


<img src="https://github.com/downtail/ItemDecorationPlus/blob/master/screenshots/cosmetic.gif" alt="image"
width="160" height="320"/>

<img src="https://github.com/downtail/ItemDecorationPlus/blob/master/screenshots/expand.gif" alt="image"
width="160" height="320"/>

<img src="https://github.com/downtail/ItemDecorationPlus/blob/master/screenshots/mix.gif" alt="image"
width="160" height="320"/>

### 使用方式

1. 添加jitpack仓库  
```
allprojects {  
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
  
2. 添加依赖

```
implementation 'com.github.downtail:ItemDecorationPlus:0.1.2'

```

3. 实现接口
```
public class MaskedAdapter extends RecyclerView.Adapter<MaskedAdapter.SampleHolder> implements MaskedExtension{}

```
  public interface MaskedExtension {

    //返回true则需要实现粘性
    boolean isMaskedItem(int position);

    //粘性view的高度
    int getMaskedHeight(int position);

    //自定义粘性view的布局
    View getMaskedView(int position);

  }


4. 为RecyclerView添加ItemDecoration
```
MaskedItemDecoration maskedItemDecoration = MaskedItemDecoration.Builder
                .with(maskedAdapter)
                .setOnMaskedItemClickListener(new MaskedItemDecoration.OnMaskedItemClickListener() {
                    @Override
                    public void onMaskedItemClick(int position) {//监听粘性item中子view点击事件
                        Toast.makeText(MaskedActivity.this, "你点击了item " + position, Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnMaskedViewClickListener(new MaskedItemDecoration.OnMaskedViewClickListener() {//监听粘性item点击事件
                    @Override
                    public void onMaskedViewClick(View view, int position) {
                        if (view.getId() == R.id.tv_sample) {
                            Toast.makeText(MaskedActivity.this, "你点击了sample" + position, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build();

        rvSample.addItemDecoration(maskedItemDecoration);
```




### 参考

[StickyDecoration](https://github.com/Gavin-ZYX/StickyDecoration)
