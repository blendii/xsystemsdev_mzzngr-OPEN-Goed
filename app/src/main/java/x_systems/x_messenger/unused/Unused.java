package x_systems.x_messenger.unused;

/**
 *

 private void goToFragment(android.app.Fragment fragment, String tag) {
 android.app.Fragment instanceFragment =
 (savedInstanceState != null) ?
 getFragmentManager().getFragment(savedInstanceState, tag) :
 null;

 if (instanceFragment == null) {
 FragmentTransaction emptySupportFragment = getSupportFragmentManager().beginTransaction();
 emptySupportFragment.replace(
 R.id.fragment_container,
 new EmptyFragment.SupportFragment(),
 "EmptySupportFragment"
 );
 emptySupportFragment.commit();

 android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
 transaction.add(R.id.fragment_container, fragment, tag);
 transaction.commit();
 } else {
 //fragment = getFragmentManager().getFragment(savedInstanceState, tag);

 android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
 transaction.replace(R.id.fragment_container, instanceFragment);
 transaction.commit();
 }
 }

 private void goToFullscreenFragment(android.app.Fragment fragment) {
 android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
 transaction.replace(R.id.fragment_fullscreen, fragment);
 transaction.addToBackStack(null);
 transaction.commit();
 }

 private void goToSupportFragment(Fragment fragment, String tag) {
 android.app.FragmentTransaction emptyFragment = getFragmentManager().beginTransaction();
 emptyFragment.replace(R.id.fragment_container, new EmptyFragment.AppFragment());
 emptyFragment.commit();

 FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
 transaction.replace(R.id.fragment_container, fragment);
 transaction.commit();

 Fragment instanceFragment =
 (savedInstanceState != null) ?
 getSupportFragmentManager().getFragment(savedInstanceState, tag) :
 null;

 if (instanceFragment == null) {
 addEmptyFragment();

 android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
 transaction.add(R.id.fragment_container, fragment, tag);
 transaction.commit();
 } else {
 replaceEmptyFragment();

 FragmentTransaction transaction = getFragmentManager().beginTransaction();
 transaction.replace(R.id.fragment_container, instanceFragment);
 transaction.commit();
 }
 }

 private void addEmptyFragment() {
 android.app.FragmentTransaction emptyFragment = getFragmentManager().beginTransaction();
 emptyFragment.add(
 R.id.fragment_container,
 new EmptyFragment.AppFragment(),
 "EmptyFragment"
 );
 emptyFragment.commit();
 }

 private void replaceEmptyFragment() {
 android.app.FragmentTransaction emptyFragment = getFragmentManager().beginTransaction();
 emptyFragment.replace(
 R.id.fragment_container,
 new EmptyFragment.AppFragment(),
 "EmptyFragment"
 );
 emptyFragment.commit();
 }

 private void setEmptySupportFragment() {

 }*/
