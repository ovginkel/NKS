package com.ihpukan.nks.view.screens.main.messages;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihpukan.nks.R;
import com.ihpukan.nks.common.CommonUtils;
import com.ihpukan.nks.common.RoundedBackgroundSpan;
import com.ihpukan.nks.emoji.CustomTarget;
import com.ihpukan.nks.emoji.Lookup;
import com.ihpukan.nks.emoji.LookupCheatSheet;
import com.ihpukan.nks.emoji.LookupLocal;
import com.ihpukan.nks.emoji.LookupLocalNakuphi;
import com.ihpukan.nks.model.Attachment;
import com.ihpukan.nks.model.MembersWrapper;
import com.ihpukan.nks.model.Message;
import com.ihpukan.nks.model.Profile;
import com.ihpukan.nks.model.SlackFile;
import com.ihpukan.nks.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<Message> messages;
    private OnSendClickListener clickListener;
    private MembersWrapper myMembersWrapper;

    public MessagesAdapter(OnSendClickListener clickListener) {
        this(null, null, clickListener);
    }

    public MessagesAdapter(List<Message> mmessages, MembersWrapper users, OnSendClickListener clickListener) {
        this.clickListener = clickListener;
        addMessages(mmessages,users);
    }

    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_view, parent, false);
        return new MessagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessagesAdapter.ViewHolder holder, int position) {
        final Message message = messages.get(position);
        boolean isNamed = false;

        holder.sentTime.setText(CommonUtils.getDateCurrentTimeZone(Long.parseLong(message.ts.split("\\.",2)[0]),"E, d MMM yyyy HH:mm:ss"/*"yyyy-MM-dd HH:mm:ss"*/));//sentDate.toString());

        String TextAll = "";
        if(message.text != null) {
            //holder.messageText.setText(message.text);
            TextAll = message.text;
        }

        if(message.attachments != null)
        {
            //TextAll = "";
            for (Attachment attachment: message.attachments) {
                if(attachment.text != null)
                {
                    TextAll += (!TextAll.contains(attachment.text))?((TextAll.length()>0?"\n":"")+(attachment.title!=null?((!attachment.text.contains(attachment.title))?attachment.title+"\n":""):"")+attachment.text):"";
                }
                else if(attachment.fallback != null)
                {
                    TextAll += (!TextAll.contains(attachment.fallback))?((TextAll.length()>0?"\n":"")+(attachment.title!=null?((!attachment.fallback.contains(attachment.title))?attachment.title+"\n":""):"")+attachment.fallback):"";
                }
            }
            //holder.messageText.setText(TextAll);
        }

        if(message.files != null)
        {
            //TextAll = "";
            for (SlackFile slackfile: message.files) {
                if(slackfile.url_private_download != null)
                {
                    TextAll += (!TextAll.contains(slackfile.url_private_download))?((TextAll.length()>0?"\n":"")+(slackfile.name!=null?((!slackfile.url_private_download.contains(slackfile.name))?slackfile.name+"\n":""):"")+slackfile.url_private_download):"";
                }
                else if(slackfile.url_private != null)
                {
                    TextAll += (!TextAll.contains(slackfile.url_private))?((TextAll.length()>0?"\n":"")+(slackfile.name!=null?((!slackfile.url_private.contains(slackfile.name))?slackfile.name+"\n":""):"")+slackfile.url_private):"";
                }
            }
            //holder.messageText.setText(TextAll);
        }

        if(message.bot_id != null?message.bot_id!="":false)
        {
            User botuser = new User();
            botuser.id = message.bot_id;
            botuser.name = message.bot_id;
            botuser.profile = new Profile();
            botuser.profile.real_name = "Bot ("+message.bot_id+")";
            myMembersWrapper.members.add(botuser);
            message.member = botuser;

        }

        ///Handle name references
        String myPatternNR = "\\<@([A-Z0-9]*)[\\|]{0,1}([A-Za-z0-9\\_]*)\\>"; //".*?FILES_SECTION.*?\n(.*?)\n.*?FILES_SECTION.*?";
        Pattern pNR = Pattern.compile(myPatternNR);
        Matcher mNR = pNR.matcher(TextAll);
        int replaceCounter = 0;
        String specialName = "";
        while(mNR.find()) {
            Boolean isReplaced = false;

            if (mNR.group(2) != null ? mNR.group(2).length() > 0 : false) {
                String replaceName = mNR.group(2).substring(0, 1).toUpperCase() + (mNR.group(2).length() > 1 ? mNR.group(2).substring(1, mNR.group(2).length()).toLowerCase() : "");
                TextAll = TextAll.replaceFirst(myPatternNR, replaceName);
                mNR = pNR.matcher(TextAll);
                if(replaceCounter==0)
                {
                    specialName = replaceName;
                }
                replaceCounter++;
                isReplaced = true;
            } else //Have to lookup user on id
            {
                /*for (int i = 0; i < messages.size(); i++) //Only look in older messages
                {
                    if (messages.get(i) != null ? (messages.get(i).user != null ? mNR.group(1) != null : false) : false) //Fix crash on null id situation
                    {
                        if (messages.get(i).user.equalsIgnoreCase(mNR.group(1)) ? messages.get(i).member != null : false) {
                            TextAll = TextAll.replaceFirst(myPatternNR, messages.get(i).member.profile.real_name);
                            mNR = pNR.matcher(TextAll); //Update
                            i = messages.size(); //Force out of loop.
                            isReplaced = true;
                        }
                    }
                }*/
                if(myMembersWrapper!=null?myMembersWrapper.members!=null:false)
                {
                    for(int i=0 ; i<myMembersWrapper.members.size(); i++)
                    {
                        if (myMembersWrapper.members.get(i) != null?(myMembersWrapper.members.get(i).id!=null?myMembersWrapper.members.get(i).id.equalsIgnoreCase(mNR.group(1)):false):false) {
                            String replaceName = myMembersWrapper.members.get(i).profile.real_name;
                            TextAll = TextAll.replaceFirst(myPatternNR, replaceName);
                            mNR = pNR.matcher(TextAll); //Update
                            i = myMembersWrapper.members.size(); //Force out of loop.
                            if(replaceCounter==0)
                            {
                                specialName = replaceName;
                            }
                            replaceCounter++;
                            isReplaced = true;
                        }
                    }
                }
            }
            if (!isReplaced)
            {
                String replaceName = '@' + (mNR.group(1) != null ? mNR.group(1) : "");
                TextAll = TextAll.replaceFirst(myPatternNR, replaceName);
                if(replaceCounter==0)
                {
                    specialName = replaceName;
                }
                replaceCounter++;
                mNR = pNR.matcher(TextAll); //Update
            }

        }

        if(message.subtype!=null?message.subtype.equalsIgnoreCase("file_comment"):false) //Handle special case with no user attributed directly (only in message)
        {
            holder.senderOfMessage.setText(specialName);
            isNamed = true;
        }


        if(!isNamed) {
            if (myMembersWrapper != null ? myMembersWrapper.members != null : false) {
                for (int i = 0; i < myMembersWrapper.members.size(); i++) {
                    if (myMembersWrapper.members.get(i) != null ? (myMembersWrapper.members.get(i).id != null ? myMembersWrapper.members.get(i).id.equalsIgnoreCase(message.user != null ? message.user : "NOBODY") : false) : false) {
                        holder.senderOfMessage.setText(myMembersWrapper.members.get(i).profile.real_name != null ? myMembersWrapper.members.get(i).profile.real_name : "NOBODY");
                        isNamed = (myMembersWrapper.members.get(i).profile.real_name != null);
                    }
                }
            }
        }
            if(!isNamed) {
            if (message.member != null) {
                holder.senderOfMessage.setText(message.member.profile.real_name);
            } else if (message.user != null) {
                holder.senderOfMessage.setText(message.user);
            } else if (message.username != null) {
                holder.senderOfMessage.setText(message.username);
            }
        }

        ///Filter out skin tone nonsense for now
        String myPatternSK = "\\:skin\\-tone[^:]*\\:"; //".*?FILES_SECTION.*?\n(.*?)\n.*?FILES_SECTION.*?";
        Pattern pSK = Pattern.compile(myPatternSK);
        Matcher mSK = pSK.matcher(TextAll);
        while(mSK.find())
        {
            if (mSK.group(0) != null ? mSK.group(0).length() > 0 : false) {
                TextAll = TextAll.replaceAll(myPatternSK,"");
                mSK = pSK.matcher(TextAll);
            }
        }



        ///Handle links to images (png, bmp, jpg, jpeg) or other links //(\.png|\.bmp|\.jpg|\.jpeg)
        String myPatternImg = "\\<([^|]*)[^>]*\\>";
        Pattern pImg = Pattern.compile(myPatternImg);
        //List<CustomTarget> targetsImg = new ArrayList<CustomTarget>();
        //SpannableString ssb = new SpannableString( TextAll  );
        Matcher mImg = pImg.matcher(TextAll);
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") List<CustomTarget> targets = new ArrayList<>();
        //holder.messageText.setText( ssb, TextView.BufferType.SPANNABLE );
        while(mImg.find()) {
            /*CustomTarget inTextImg = new CustomTarget(mImg.group(1), mImg.start(), mImg.end(), holder.messageText, ssb, 200, 200);
            targets.add(inTextImg);
            String imgUrl = mImg.group(1);
            Picasso.with(holder.messageText.getContext())
                    .load(imgUrl)
                    .into(inTextImg);*/ //Slack online files protected behind password
            //Add a space to end if this was not there:
            if(mImg.end()<TextAll.length()?!(TextAll.substring(mImg.end(),mImg.end()+1).equalsIgnoreCase("")):false)
            {
                TextAll = TextAll.replaceFirst(myPatternImg ,mImg.group(1)+" ");
            }
            else {
                TextAll = TextAll.replaceFirst(myPatternImg ,mImg.group(1));
            }
            mImg = pImg.matcher(TextAll);
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder( TextAll );

        ///Handle bold, italic, striketrough, code and quote

        //Multi paragraph
        String myPatternQTM = "\\&gt;\\&gt;\\&gt;([^($)]*)$"; //(\_|\~)
        Pattern pQTM = Pattern.compile(myPatternQTM);
        Matcher mQTM = pQTM.matcher(ssb);
        while(mQTM.find())
        {
            ssb.delete(mQTM.start(),mQTM.start()+12);
            //ssb.delete(mCD.end()-2,mCD.end()-1);
            //ssb.setSpan(new BackgroundColorSpan(Color.WHITE), mQTM.start(), mQTM.end() - 12, 0);
            if(ssb.length()>=(mQTM.end() - 12)) { //Safety catch
                ssb.setSpan(new QuoteSpan(Color.WHITE), mQTM.start(), mQTM.end() - 12, 0);
            }
            mQTM = pQTM.matcher(ssb);
        }

        //Single paragraph
        String myPatternQT = "\\&gt;([^(\n)]*)"; //(\_|\~)
        Pattern pQT = Pattern.compile(myPatternQT,Pattern.MULTILINE);
        Matcher mQT = pQT.matcher(ssb);
        while(mQT.find())
        {
            ssb.delete(mQT.start(),mQT.start()+4);
            //ssb.delete(mCD.end()-2,mCD.end()-1);
            //ssb.setSpan(new BackgroundColorSpan(Color.WHITE), mQT.start(), mQT.end() - 4, 0);
            if(ssb.length()>=(mQT.end() - 4)) { //Safety catch
                ssb.setSpan(new QuoteSpan(Color.WHITE), mQT.start(), mQT.end() - 4, 0);
            }
            mQT = pQT.matcher(ssb);
        }

        String myPatternCDM = "\\`\\`\\`([^`]*)\\`\\`\\`"; //(\_|\~) //Code or preformatted
        Pattern pCDM = Pattern.compile(myPatternCDM);
        Matcher mCDM = pCDM.matcher(ssb);
        while(mCDM.find())
        {
            ssb.delete(mCDM.start(),mCDM.start()+3); //Remove ``` at start
            ssb.delete(mCDM.end()-6,mCDM.end()-3); //Remove ``` at end
            //ssb.setSpan(new BackgroundColorSpan(Color.GREEN), mCDM.start(), mCDM.end() - 2, 0);
            if(ssb.length()>=(mCDM.end() - 6)) { //Safety catch
                ssb.setSpan(new RoundedBackgroundSpan(Color.WHITE, Color.BLUE), mCDM.start(), mCDM.end() - 6, 0);
                ssb.setSpan(new TypefaceSpan("monospace"), mCDM.start(), mCDM.end() - 6, 0);
                ssb.setSpan(new RelativeSizeSpan(0.4f), mCDM.start(), mCDM.end() - 6, 0);
            }
            mCDM = pCDM.matcher(ssb);
        }

        String myPatternCD = "\\`([^`]*)\\`"; //(\_|\~) //Code or preformatted
        Pattern pCD = Pattern.compile(myPatternCD);
        Matcher mCD = pCD.matcher(ssb);
        while(mCD.find())
        {
            ssb.delete(mCD.start(),mCD.start()+1);
            ssb.delete(mCD.end()-2,mCD.end()-1);
            //ssb.setSpan(new BackgroundColorSpan(Color.GREEN), mCD.start(), mCD.end() - 2, 0);
            if(ssb.length()>=(mCD.end() - 2)) { //Safety catch
                ssb.setSpan(new RoundedBackgroundSpan(Color.WHITE, Color.BLUE), mCD.start(), mCD.end() - 2, 0);
                ssb.setSpan(new TypefaceSpan("monospace"), mCD.start(), mCD.end() - 2, 0);
                ssb.setSpan(new RelativeSizeSpan(0.4f), mCD.start(), mCD.end() - 2, 0);
            }
            mCD = pCD.matcher(ssb);
        }

        String myPatternBD = "\\*([A-Za-z0-9 ]*)\\*"; //(\_|\~)
        Pattern pBD = Pattern.compile(myPatternBD);
        Matcher mBD = pBD.matcher(ssb);
        while(mBD.find())
        {
            if(((mBD.end()<ssb.length())?(ssb.charAt(mBD.end())=='\n'||ssb.charAt(mBD.end())==' '||ssb.charAt(mBD.end())=='_'||ssb.charAt(mBD.end())=='~'):false)||(mBD.start()>0?(ssb.charAt(mBD.start()-1)=='\n'||ssb.charAt(mBD.start()-1)==' '||ssb.charAt(mBD.start()-1)=='_'||ssb.charAt(mBD.start()-1)=='~'):false))
            {
                ssb.delete(mBD.start(),mBD.start()+1);
                ssb.delete(mBD.end()-2,mBD.end()-1);
                if(ssb.length()>=(mBD.end() - 2)) { //Safety catch
                    ssb.setSpan(new StyleSpan(Typeface.BOLD), mBD.start(), mBD.end() - 2, 0);
                }
                mBD = pBD.matcher(ssb);
            }
        }

        String myPatternIT = "\\_([A-Za-z0-9 ]*)\\_"; //(\*|\~)
        Pattern pIT = Pattern.compile(myPatternIT);
        Matcher mIT = pIT.matcher(ssb);
        while(mIT.find())
        {
            if(((mIT.end()<ssb.length())?(ssb.charAt(mIT.end())=='\n'||ssb.charAt(mIT.end())==' '||ssb.charAt(mIT.end())=='*'||ssb.charAt(mIT.end())=='~'):false)||(mIT.start()>0?(ssb.charAt(mIT.start()-1)=='\n'||ssb.charAt(mIT.start()-1)==' '||ssb.charAt(mIT.start()-1)=='*'||ssb.charAt(mIT.start()-1)=='~'):false))
            {
                ssb.delete(mIT.start(),mIT.start()+1);
                ssb.delete(mIT.end()-2,mIT.end()-1);
                if(ssb.length()>=(mIT.end() - 2)) { //Safety catch
                    ssb.setSpan(new StyleSpan(Typeface.ITALIC), mIT.start(), mIT.end() - 2, 0);
                }
                mIT = pIT.matcher(ssb);
            }
        }

        String myPatternST = "\\~([A-Za-z0-9 ]*)\\~"; //( |\*|\_)
        Pattern pST = Pattern.compile(myPatternST);
        Matcher mST = pST.matcher(ssb);
        while(mST.find())
        {
            if(((mST.end()<ssb.length())?(ssb.charAt(mST.end())=='\n'||ssb.charAt(mST.end())==' '||ssb.charAt(mST.end())=='*'||ssb.charAt(mST.end())=='_'):false)||(mST.start()>0?(ssb.charAt(mST.start()-1)=='\n'||ssb.charAt(mST.start()-1)==' '||ssb.charAt(mST.start()-1)=='*'||ssb.charAt(mST.start()-1)=='_'):false))
            {
                ssb.delete(mST.start(),mST.start()+1);
                ssb.delete(mST.end()-2,mST.end()-1);
                if(ssb.length()>=(mST.end() - 2)) { //Safety catch
                    ssb.setSpan(new StrikethroughSpan(), mST.start(), mST.end() - 2, 0);
                }
                mST = pST.matcher(ssb);
            }
        }

        mIT = pIT.matcher(ssb);
        while(mIT.find())
        {
            if(((mIT.end()<ssb.length())?(ssb.charAt(mIT.end())=='\n'||ssb.charAt(mIT.end())==' '||ssb.charAt(mIT.end())=='*'||ssb.charAt(mIT.end())=='~'):false)||(mIT.start()>0?(ssb.charAt(mIT.start()-1)=='\n'||ssb.charAt(mIT.start()-1)==' '||ssb.charAt(mIT.start()-1)=='*'||ssb.charAt(mIT.start()-1)=='~'):false))
            {
                ssb.delete(mIT.start(),mIT.start()+1);
                ssb.delete(mIT.end()-2,mIT.end()-1);
                if(ssb.length()>=(mIT.end() - 2)) { //Safety catch
                    ssb.setSpan(new StyleSpan(Typeface.ITALIC), mIT.start(), mIT.end() - 2, 0);
                }
                mIT = pIT.matcher(ssb);
            }
        }

        mBD = pBD.matcher(ssb);
        while(mBD.find())
        {
            if(((mBD.end()<ssb.length())?(ssb.charAt(mBD.end())=='\n'||ssb.charAt(mBD.end())==' '||ssb.charAt(mBD.end())=='_'||ssb.charAt(mBD.end())=='~'):false)||(mBD.start()>0?(ssb.charAt(mBD.start()-1)=='\n'||ssb.charAt(mBD.start()-1)==' '||ssb.charAt(mBD.start()-1)=='_'||ssb.charAt(mBD.start()-1)=='~'):false))
            {
                ssb.delete(mBD.start(),mBD.start()+1);
                ssb.delete(mBD.end()-2,mBD.end()-1);
                if(ssb.length()>=(mBD.end() - 2)) { //Safety catch
                    ssb.setSpan(new StyleSpan(Typeface.BOLD), mBD.start(), mBD.end() - 2, 0);
                }
                mBD = pBD.matcher(ssb);
            }
        }

        mIT = pIT.matcher(ssb);
        while(mIT.find())
        {
            if(((mIT.end()<ssb.length())?(ssb.charAt(mIT.end())=='\n'||ssb.charAt(mIT.end())==' '||ssb.charAt(mIT.end())=='*'||ssb.charAt(mIT.end())=='~'):false)||(mIT.start()>0?(ssb.charAt(mIT.start()-1)=='\n'||ssb.charAt(mIT.start()-1)==' '||ssb.charAt(mIT.start()-1)=='*'||ssb.charAt(mIT.start()-1)=='~'):false))
            {
                ssb.delete(mIT.start(),mIT.start()+1);
                ssb.delete(mIT.end()-2,mIT.end()-1);
                if(ssb.length()>=(mIT.end() - 2)) { //Safety catch
                    ssb.setSpan(new StyleSpan(Typeface.ITALIC), mIT.start(), mIT.end() - 2, 0);
                }
                mIT = pIT.matcher(ssb);
            }
        }

        ///Implement styled version with emoticons
        String myPattern = "\\:[A-Za-z0-9\\d\\-\\+\\_]*\\:";
        Pattern p = Pattern.compile(myPattern);

        Matcher m = p.matcher(ssb);
        holder.messageText.setText( ssb, TextView.BufferType.SPANNABLE );
        while(m.find())
        {
            CustomTarget inTextEmoji = new CustomTarget(m.group(0),m.start(),m.end(),holder.messageText,ssb, 32, 32);
            targets.add(inTextEmoji);

            //Load from local resource set 0
            String findMoji = m.group(0).substring(1, m.group(0).length() - 1);

            Integer resid = LookupLocalNakuphi.emojiResourceID.get(findMoji);
            if (resid != null) {
                Picasso.with(holder.messageText.getContext())
                        .load(resid)
                        .into(inTextEmoji);
            } else {
                //Load from local resource set 1
                resid = LookupCheatSheet.emojiResourceID.get(findMoji);
                if (resid != null) {
                    Picasso.with(holder.messageText.getContext())
                            .load(resid)
                            .into(inTextEmoji);
                } else {
                    //Load from local resource set 2
                    resid = LookupLocal.emojiResourceID.get(findMoji);
                    if (resid != null) {
                        Picasso.with(holder.messageText.getContext())
                                .load(resid)
                                .into(inTextEmoji);
                    } else { //First try hyphen then try loading from online lookup or custom team emoji urls
                        String findMojihyphen = findMoji.replaceAll("-","_");
                        resid = LookupLocalNakuphi.emojiResourceID.get(findMojihyphen);

                        if (resid != null) {
                            Picasso.with(holder.messageText.getContext())
                                    .load(resid)
                                    .into(inTextEmoji);
                        } else {
                            //Load from local resource set 1
                            resid = LookupCheatSheet.emojiResourceID.get(findMojihyphen);
                            if (resid != null) {
                                Picasso.with(holder.messageText.getContext())
                                        .load(resid)
                                        .into(inTextEmoji);
                            } else {
                                //Load from local resource set 2
                                resid = LookupLocal.emojiResourceID.get(findMojihyphen);
                                if (resid != null) {
                                    Picasso.with(holder.messageText.getContext())
                                            .load(resid)
                                            .into(inTextEmoji);
                                } else { //Load from online lookup or custom team emoji urls
                                    String url = Lookup.emojiUrl.get(findMojihyphen);
                                    if (url != null) {
                                        Picasso.with(holder.messageText.getContext())
                                                .load(url)
                                                .into(inTextEmoji);
                                    }
                                    else {
                                       url = Lookup.emojiUrl.get(findMoji);
                                        if (url != null) {
                                            Picasso.with(holder.messageText.getContext())
                                                    .load(url)
                                                    .into(inTextEmoji);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        targets.clear();

        Linkify.addLinks( holder.messageText, Linkify.ALL );

        if(message.icons != null)
        {
            Picasso.with(holder.itemView.getContext()).
                    load(message.icons.image_48).
                    into(holder.imageViewMessageProfile);
        }
        else if(message.member != null)
        {
            if((message.member.profile != null)?(message.member.profile.image_48!=null):false) {
                Picasso.with(holder.itemView.getContext()).
                        load(message.member.profile.image_48).
                        into(holder.imageViewMessageProfile);
            }
            else
            {
                holder.imageViewMessageProfile.setImageResource(R.drawable.default_profile);
            }
        }
        else
        {
            holder.imageViewMessageProfile.setImageResource(R.drawable.default_profile);
        }

    }

    public void addMessages(List<Message> mmessages, MembersWrapper musers) {
        if (this.messages != null) {
            this.messages.clear();
            this.messages.addAll(mmessages);
        } else {
            if(mmessages != null) {
                this.messages = new ArrayList<>(mmessages.size());
                this.messages.addAll(mmessages);
            }
            else
            {
                this.messages = new ArrayList<Message>();
            }
        }
        if(musers!=null) {
            this.myMembersWrapper = musers;
        }
        notifyDataSetChanged();
    }

    public void clearMessages() {
        if (messages == null){
            this.notifyDataSetChanged();
            return;
        }
        messages.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageViewMessageProfile)
        ImageView imageViewMessageProfile;
        @BindView(R.id.senderOfMessage)
        TextView senderOfMessage;
        @BindView(R.id.sentTime)
        TextView sentTime;
        @BindView(R.id.messageText)
        EditText messageText;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
