package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wtz.tools.R;
import com.wtz.tools.Span.AlignCenterImageSpan;
import com.wtz.tools.Span.BgColorTextSpan;
import com.wtz.tools.Span.WrapTextBgColorSpan;

public class SpannableFragment extends Fragment {
    private static final String TAG = SpannableFragment.class.getSimpleName();

    private TextView mSpannable;
    
    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_spannable, container, false);

        mSpannable = view.findViewById(R.id.tv_spannable);
        test(getActivity());

        return view;
    }

    public void test(Context context) {
        // 将 TextView 换成 EditText，就可以实现触摸时选中文字
        TextView textView = mSpannable;
        textView.setLineSpacing(0, 1.5f);// 为了看对齐等效果
        // 在单击链接时凡是有要执行的动作，都必须设置MovementMethod对象
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        // 设置点击后的颜色，这里涉及到ClickableSpan的点击背景
        textView.setHighlightColor(0xff8FABCC);
        textView.setText(generateSpannableString(context, textView));
    }

    private SpannableString generateSpannableString(final Context context, TextView textView) {
        StringBuilder builder = new StringBuilder();

        final String month = "7月";
        builder.append(month);
        final String day = "8日";
        builder.append(day);
        builder.append("\n");

        final String bg1 = "背景色1";
        builder.append(bg1);
        final String bg2 = "背景色2";
        builder.append(bg2);
        builder.append("\n");

        final String bg_color_text_place0 = "置顶";
        builder.append(bg_color_text_place0);
        final String bg_color_text_place1 = "热";
        builder.append(bg_color_text_place1);
        builder.append("某某产品");
        builder.append("\n");

        final String style_normal = "一般";
        builder.append(style_normal);
        final String style_bold = "粗体";
        builder.append(style_bold);
        final String style_italic = "斜体";
        builder.append(style_italic);
        final String style_bold_italic = "粗斜体";
        builder.append(style_bold_italic);
        builder.append("\n");

        final String under_line = "下划线";
        builder.append(under_line);
        final String strike_through_line = "删除线";
        builder.append(strike_through_line);
        final String url_jump = "链接";
        builder.append(url_jump);
        builder.append("\n");

        builder.append("Hello!");
        final String icon_place0 = "icon0";
        builder.append(icon_place0);
        builder.append("你好啊！");
        builder.append("\n");

        builder.append("Hello!");
        final String icon_place1 = "icon1";
        builder.append(icon_place1);
        builder.append("你好啊！");
        builder.append("\n");

        builder.append("Hello!");
        final String icon_place2 = "icon2";
        builder.append(icon_place2);
        builder.append("你好啊！");
        builder.append("\n");

        builder.append("下");
        final String sub_label = "标1";
        builder.append(sub_label);
        builder.append("上");
        final String super_label = "标2";
        builder.append(super_label);
        builder.append("新消息");
        final String super_msg = "●";
        builder.append(super_msg);
        builder.append("\n");

        final String click_me = "点击我吧！";
        builder.append(click_me);
        builder.append("\n");

        final String blur1 = "模糊1";
        builder.append(blur1);
        final String blur2 = "模糊2";
        builder.append(blur2);
        final String blur3 = "模糊3";
        builder.append(blur3);
        builder.append("\n");

        String content = builder.toString();

        int monthStart = content.indexOf(month);
        int monthEnd = monthStart + month.length();
        int dayStart = content.indexOf(day);
        int dayEnd = dayStart + day.length();

        int bgStart1 = content.indexOf(bg1);
        int bgEnd1 = bgStart1 + bg1.length();
        int bgStart2 = content.indexOf(bg2);
        int bgEnd2 = bgStart2 + bg2.length();

        int bg_color_text_start0 = content.indexOf(bg_color_text_place0);
        int bg_color_text_end0 = bg_color_text_start0 + bg_color_text_place0.length();
        int bg_color_text_start1 = content.indexOf(bg_color_text_place1);
        int bg_color_text_end1 = bg_color_text_start1 + bg_color_text_place1.length();

        int style_normal_start = content.indexOf(style_normal);
        int style_normal_end = style_normal_start + style_normal.length();
        int style_bold_start = content.indexOf(style_bold);
        int style_bold_end = style_bold_start + style_bold.length();
        int style_italic_start = content.indexOf(style_italic);
        int style_italic_end = style_italic_start + style_italic.length();
        int style_bold_italic_start = content.indexOf(style_bold_italic);
        int style_bold_italic_end = style_bold_italic_start + style_bold_italic.length();

        int under_line_start = content.indexOf(under_line);
        int under_line_end = under_line_start + under_line.length();
        int strike_through_line_start = content.indexOf(strike_through_line);
        int strike_through_line_end = strike_through_line_start + strike_through_line.length();
        int url_jump_start = content.indexOf(url_jump);
        int url_jump_end = url_jump_start + url_jump.length();

        int start0 = content.indexOf(icon_place0);
        int end0 = start0 + icon_place0.length();
        int start1 = content.indexOf(icon_place1);
        int end1 = start1 + icon_place1.length();
        int start2 = content.indexOf(icon_place2);
        int end2 = start2 + icon_place2.length();

        int sub_label_start = content.indexOf(sub_label);
        int sub_label_end = sub_label_start + sub_label.length();
        int super_label_start = content.indexOf(super_label);
        int super_label_end = super_label_start + super_label.length();
        int super_msg_start = content.indexOf(super_msg);
        int super_msg_end = super_msg_start + super_msg.length();

        int click_me_start = content.indexOf(click_me);
        int click_me_end = click_me_start + click_me.length();

        int blur1_start = content.indexOf(blur1);
        int blur1_end = blur1_start + blur1.length();
        int blur2_start = content.indexOf(blur2);
        int blur2_end = blur2_start + blur2.length();
        int blur3_start = content.indexOf(blur3);
        int blur3_end = blur3_start + blur3.length();

        int textColor = Color.parseColor("#FFFFFF");
        float textSize = textView.getTextSize();
        BgColorTextSpan bgColorTextSpan0 = new BgColorTextSpan(context, context.getResources().getColor(R.color.colorPrimary), textColor, textSize, bg_color_text_place0);
        bgColorTextSpan0.setRightMarginDpValue(5);
        BgColorTextSpan bgColorTextSpan1 = new BgColorTextSpan(context, context.getResources().getColor(R.color.colorAccent), textColor, textSize, bg_color_text_place1);
        bgColorTextSpan1.setRightMarginDpValue(5);

        // 未指定图片大小
        ImageSpan imageSpan0 = new ImageSpan(context, R.mipmap.ic_launcher_round, DynamicDrawableSpan.ALIGN_BOTTOM);

        // 设定图片大小
        Drawable fuDrawable1 = context.getResources().getDrawable(R.mipmap.ic_launcher_round);
        int fontHeight = getFontHeight(textView.getTextSize());
        fuDrawable1.setBounds(0, 0, fontHeight, fontHeight);
        ImageSpan imageSpan1 = new ImageSpan(fuDrawable1);

        // 测试对齐方式
        Drawable fuDrawable2 = context.getResources().getDrawable(R.mipmap.ic_launcher_round);
//        fuDrawable2.setBounds(0, 0, 50, 50);// 这个大小看对齐方式明显
        fuDrawable2.setBounds(0, 0, fontHeight, fontHeight);
//        // ALIGN_BOTTOM
//        ImageSpan imageSpan2 = new ImageSpan(fuDrawable2, ALIGN_BOTTOM);
//        // ALIGN_BASELINE
//        ImageSpan imageSpan2 = new ImageSpan(fuDrawable2, ALIGN_BASELINE);
        // 自定义居中对齐
        AlignCenterImageSpan imageSpan2 = new AlignCenterImageSpan(fuDrawable2);

        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new RelativeSizeSpan(2.0f), monthStart, monthEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ForegroundColorSpan(Color.RED), dayStart, dayEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), bgStart1, bgEnd1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new WrapTextBgColorSpan(Color.GREEN), bgStart2, bgEnd2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(bgColorTextSpan0, bg_color_text_start0, bg_color_text_end0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(bgColorTextSpan1, bg_color_text_start1, bg_color_text_end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new StyleSpan(Typeface.NORMAL), style_normal_start, style_normal_end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), style_bold_start, style_bold_end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), style_italic_start, style_italic_end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), style_bold_italic_start, style_bold_italic_end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        spannableString.setSpan(new UnderlineSpan(), under_line_start, under_line_end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new StrikethroughSpan(), strike_through_line_start, strike_through_line_end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new URLSpan("https://www.baidu.com/"), url_jump_start, url_jump_end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        // 在单击链接时凡是有要执行的动作，都必须设置MovementMethod对象
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
//        // 设置点击后的颜色，这里涉及到ClickableSpan的点击背景
//        textView.setHighlightColor(0xff8FABCC);

        spannableString.setSpan(imageSpan0, start0, end0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(imageSpan1, start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(imageSpan2, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new SubscriptSpan(), sub_label_start, sub_label_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), sub_label_start, sub_label_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new SuperscriptSpan(), super_label_start, super_label_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), super_label_start, super_label_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new SuperscriptSpan(), super_msg_start, super_msg_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), super_msg_start, super_msg_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                TextView tv = (TextView) widget;
                tv.getText().subSequence(tv.getSelectionStart(), tv.getSelectionEnd());
                Toast.makeText(context, "你点击了我！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#abc123"));
                ds.setUnderlineText(true);
            }
        }, click_me_start, click_me_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new MaskFilterSpan(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL)),
                blur1_start, blur1_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new MaskFilterSpan(new BlurMaskFilter(10, BlurMaskFilter.Blur.INNER)),
                blur2_start, blur2_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new MaskFilterSpan(new BlurMaskFilter(10, BlurMaskFilter.Blur.OUTER)),
                blur3_start, blur3_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    //精确获取字体的高度
    public int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }
}
