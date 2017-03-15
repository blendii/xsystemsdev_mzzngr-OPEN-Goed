package x_systems.x_messenger.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import x_systems.x_messenger.R;
import x_systems.x_messenger.fragments.support.v4.SupportFragment;
import x_systems.x_messenger.preferences.PgpSettingsPreference;

/**
 * Created by Manasseh on 11/24/2016.
 */

public class FragmentNavigation {
    public static boolean isEnabled = false;
    private static final String EMPTY_FRAGMENT = "empty_fragment";
    private static final String EMPTY_FRAGMENT_SUPPORT = "empty_support_fragment";
    private static List<Fragment> Fragments = new ArrayList<>();
    private static List<android.support.v4.app.Fragment> SupportFragments = new ArrayList<>();
    private static final int RESOURCE_ID_CONTAINER_MAIN = R.id.fragment_container;
    private static final int RESOURCE_ID_CONTAINER_SUB = R.id.fragment_fullscreen;

    private Bundle savedInstanceState;
    private AppCompatActivity activity;

    public FragmentNavigation(Bundle savedInstanceState, AppCompatActivity activity) {
        this.savedInstanceState = savedInstanceState;
        this.activity = activity;
    }

    public void navigateDefaultFragment() {
        ContactsFragment supportFragment = new ContactsFragment();
        if (supportFragment.isAdded())
            showFragment(supportFragment);
        else {
            String tag = supportFragment.getClass().getSimpleName();
            System.out.println(tag);

            SupportFragment instanceSupportFragment = getSupportFragment(savedInstanceState, tag);

            if (instanceSupportFragment == null) {

                addSupportFragment(supportFragment, tag);
            } else {
                if (instanceSupportFragment.isAdded())
                    showFragment(instanceSupportFragment);
                else
                    replaceSupportFragment(instanceSupportFragment, tag);
            }
        }
    }

    public void navigateAsMainFragment(Fragment fragment) {
        System.out.println("Fragment");

        if (fragment.isAdded())
            showFragment(fragment);
        else if (isEnabled) {
            String tag = fragment.getClass().getSimpleName();
            System.out.println(tag);
            Fragment instanceFragment = getFragment(savedInstanceState, tag);

            if (instanceFragment == null) {
                //addEmptySupportFragment();
                //replaceEmptySupportFragment();
                System.out.println("add");
                addFragment(fragment, tag);
                showFragment(fragment);
            } else {
                //replaceEmptySupportFragment();

                if (instanceFragment.isAdded()) {
                        System.out.println("show");
                        showFragment(instanceFragment);
                }
                else {
                    System.out.println("replace");
                    replaceFragment(instanceFragment, tag);
                }
            }
        }

    }
    public void navigateAsMainFragment(SupportFragment supportFragment) {
        System.out.println("SupportFragment");
        if (supportFragment.isAdded())
            showFragment(supportFragment);
        else if (isEnabled && !supportFragment.isAdded()) {
            String tag = supportFragment.getClass().getSimpleName();
            System.out.println(tag);
            SupportFragment instanceSupportFragment = getSupportFragment(savedInstanceState, tag);

            if (instanceSupportFragment == null) {
                //addEmptyFragment();
                //replaceEmptyFragment();
                System.out.println("add");
                addSupportFragment(supportFragment, tag);
                showFragment(supportFragment);
            } else {
                //replaceEmptyFragment();

                if (instanceSupportFragment.isAdded()) {
                    System.out.println("show");
                    showFragment(instanceSupportFragment);
                }
                else {
                    System.out.println("replace");
                    replaceSupportFragment(instanceSupportFragment, tag);
                }
            }
        }
    }

    public void navigateAsSubFragment(Fragment fragment) {
        if (isEnabled) {
            String tag = fragment.getClass().getSimpleName();
            System.out.println(tag);
            Fragment instanceFragment = getFragment(savedInstanceState, tag);

            if (instanceFragment == null) {
                System.out.println("add");
                addSubFragment(fragment, tag);
            } else {
                if (instanceFragment.isAdded()) {
                    System.out.println("show");
                    showSubFragment(instanceFragment);
                }
                else {
                    System.out.println("replace");
                    replaceSubFragment(instanceFragment, tag);
                }
            }
        }
    }
    private void addSubFragment(Fragment fragment, String tag) {
        FragmentManager manager = activity.getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(RESOURCE_ID_CONTAINER_SUB, fragment, tag);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
    private void replaceSubFragment(Fragment fragment, String tag) {
        FragmentManager manager = activity.getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(RESOURCE_ID_CONTAINER_SUB, fragment, tag);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
    public void navigateAsSubFragment(SupportFragment fragment) {
        if (isEnabled) {
            android.support.v4.app.FragmentManager supportManager = activity.getSupportFragmentManager();

            android.support.v4.app.FragmentTransaction transaction = supportManager.beginTransaction();
            transaction.replace(RESOURCE_ID_CONTAINER_SUB, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        throw new UnsupportedOperationException();
    }

    private Fragment getFragment(Bundle savedInstanceState, String tag) {
        FragmentManager manager = activity.getFragmentManager();

        if (manager.findFragmentByTag(tag) != null)
            return manager.findFragmentByTag(tag);
        else
        if (savedInstanceState == null)
            return null;
        else if (manager.getFragment(savedInstanceState, tag) != null)
            return activity.getFragmentManager().getFragment(savedInstanceState, tag);
        else
            return null;
    }
    private SupportFragment getSupportFragment(Bundle savedInstanceState, String tag) {
        android.support.v4.app.FragmentManager supportManager = activity.getSupportFragmentManager();

        if (supportManager.findFragmentByTag(tag) != null)
            return (SupportFragment) supportManager.findFragmentByTag(tag);
        else
        if (savedInstanceState == null)
            return null;
        else if (supportManager.getFragment(savedInstanceState, tag) != null)
            return (SupportFragment) activity.getSupportFragmentManager().getFragment(savedInstanceState, tag);
        else
            return null;
    }

    private void addEmptyFragment() {
        FragmentTransaction emptySupportFragment = activity.getFragmentManager().beginTransaction();
        emptySupportFragment.add(
                RESOURCE_ID_CONTAINER_MAIN,
                new EmptyFragment.AppFragment(),
                EMPTY_FRAGMENT
        );
        emptySupportFragment.commit();
    }
    private void addEmptySupportFragment() {
        android.support.v4.app.FragmentManager supportManager = activity.getSupportFragmentManager();

        android.support.v4.app.FragmentTransaction emptySupportFragment = supportManager.beginTransaction();
        emptySupportFragment.add(
                RESOURCE_ID_CONTAINER_MAIN,
                new EmptyFragment.SupportFragment(),
                EMPTY_FRAGMENT_SUPPORT
        );
        emptySupportFragment.commit();
    }

    private void replaceEmptyFragment() {
        FragmentTransaction emptyFragment = activity.getFragmentManager().beginTransaction();
        emptyFragment.replace(
                RESOURCE_ID_CONTAINER_MAIN,
                new EmptyFragment.AppFragment(),
                EMPTY_FRAGMENT
        );
        emptyFragment.commit();
    }
    private void replaceEmptySupportFragment() {
        android.support.v4.app.FragmentManager supportManager = activity.getSupportFragmentManager();

        android.support.v4.app.FragmentTransaction emptySupportFragment = supportManager.beginTransaction();
        emptySupportFragment.replace(
                RESOURCE_ID_CONTAINER_MAIN,
                new EmptyFragment.SupportFragment(),
                EMPTY_FRAGMENT_SUPPORT
        );
        emptySupportFragment.commit();
    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentManager manager = activity.getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(RESOURCE_ID_CONTAINER_MAIN, fragment, tag);
        transaction.commit();
        FragmentNavigation.Fragments.add(fragment);
    }
    private void addSupportFragment(android.support.v4.app.Fragment supportFragment, String tag) {
        android.support.v4.app.FragmentManager supportManager = activity.getSupportFragmentManager();

        android.support.v4.app.FragmentTransaction transaction = supportManager.beginTransaction();
        transaction.add(RESOURCE_ID_CONTAINER_MAIN, supportFragment, tag);
        transaction.commit();
        FragmentNavigation.SupportFragments.add(supportFragment);
    }

    private void normalfragment(Fragment frag){
        FragmentManager manager = activity.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(manager.findFragmentByTag(FilesFragment.class.getSimpleName()));
        transaction.commit();
    }

    private void replaceFragment(Fragment instanceFragment, String tag) {
        FragmentManager manager = activity.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(RESOURCE_ID_CONTAINER_MAIN, instanceFragment, tag);
        transaction.commit();
    }
    private void replaceSupportFragment(android.support.v4.app.Fragment instanceSupportFragment, String tag) {
        android.support.v4.app.FragmentManager supportManager = activity.getSupportFragmentManager();

        android.support.v4.app.FragmentTransaction transaction = supportManager.beginTransaction();
        transaction.add(RESOURCE_ID_CONTAINER_MAIN, instanceSupportFragment, tag);
        transaction.commit();
    }

    private void showFragment(Fragment fragment) {
        android.support.v4.app.FragmentManager supportManager = activity.getSupportFragmentManager();
        FragmentManager manager = activity.getFragmentManager();
        android.support.v4.app.FragmentTransaction SupportTransaction = supportManager.beginTransaction();
        for(android.support.v4.app.Fragment f : FragmentNavigation.SupportFragments){
            SupportTransaction.hide(f);
        }
        SupportTransaction.commit();
        FragmentTransaction transaction = manager.beginTransaction();
        for(Fragment f : FragmentNavigation.Fragments){
            if(f != fragment){
                transaction.hide(f);
            }
        }
        transaction.show(fragment);
        transaction.commit();
    }
    private void showFragment(SupportFragment fragment) {
        android.support.v4.app.FragmentManager supportManager = activity.getSupportFragmentManager();
        FragmentManager manager = activity.getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();
        for(Fragment f : FragmentNavigation.Fragments){
            transaction.hide(f);
        }
        transaction.commit();

        android.support.v4.app.FragmentTransaction SupportTransaction = supportManager.beginTransaction();
        for(android.support.v4.app.Fragment f : FragmentNavigation.SupportFragments){
            if (f != fragment) {
                SupportTransaction.hide(f);
            }
        }
        SupportTransaction.show(fragment);
        SupportTransaction.commit();


    }

    private void showSubFragment(Fragment fragment) {
        FragmentManager manager = activity.getFragmentManager();
        manager.beginTransaction().show(fragment).commit();
    }
}