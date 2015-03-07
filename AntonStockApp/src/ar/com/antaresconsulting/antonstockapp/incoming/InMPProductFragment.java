package ar.com.antaresconsulting.antonstockapp.incoming;

import com.openerp.ConfirmMovesAsyncTask;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.antaresconsulting.antonstockapp.AntonConstants;
import ar.com.antaresconsulting.antonstockapp.R;
import ar.com.antaresconsulting.antonstockapp.R.id;
import ar.com.antaresconsulting.antonstockapp.R.layout;
import ar.com.antaresconsulting.antonstockapp.adapters.PedidoLineaAdapter;
import ar.com.antaresconsulting.antonstockapp.listener.SwipeDismissListViewTouchListener;
import ar.com.antaresconsulting.antonstockapp.model.Dimension;
import ar.com.antaresconsulting.antonstockapp.model.MateriaPrima;
import ar.com.antaresconsulting.antonstockapp.model.Pedido;
import ar.com.antaresconsulting.antonstockapp.model.PedidoLinea;
import ar.com.antaresconsulting.antonstockapp.model.SelectionObject;
import ar.com.antaresconsulting.antonstockapp.model.dao.MateriaPrimaDAO;
import ar.com.antaresconsulting.antonstockapp.model.dao.PedidoDAO;
import ar.com.antaresconsulting.antonstockapp.popup.SearchMPPopupFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class InMPProductFragment extends Fragment implements OnItemSelectedListener, InProductActions,MateriaPrimaDAO.MateriaPrimaCallbacks, PedidoDAO.PedidosCallbacks{
	private MateriaPrimaDAO mpDao;
	private PedidoDAO pedDao;

	private Spinner pedidos;
	private Spinner dimTipo;

	private ListView productos;
	//private ListView productosDispo;

	private EditText cantPlacas;
	private EditText dimH;
	private EditText dimW;
	private TextView provee;
	private Spinner dimT;


	private static final String ARG_PARAM1 = "param1";

	// TODO: Rename and change types of parameters
	private int mParam1;
	public InMPProductFragment() {

	}

	public static InMPProductFragment newInstance(int param1) {
		InMPProductFragment fragment = new InMPProductFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM1, param1);
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getInt(ARG_PARAM1);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = null;
		this.mpDao = new MateriaPrimaDAO(this);

		rootView = inflater.inflate(R.layout.fragment_in_products,	container, false);
		//rootView.findViewById(R.id.clienteRow).setVisibility(View.GONE);

		this.productos = (ListView) rootView.findViewById(R.id.productosDispo);
//		this.productosDispo.setChoiceMode(ListView.CHOICE_MODE_SINGLE);			
		this.cantPlacas = (EditText) rootView.findViewById(R.id.clientesList);
//		this.dimH = (EditText) rootView.findViewById(R.id.altoPlaca);
//		this.dimW = (EditText) rootView.findViewById(R.id.anchoPlaca);
//		this.dimT = (Spinner) rootView.findViewById(R.id.espesorPlaca);
//		this.dimT.setSelection(AntonConstants.DEFAULT_ESPESORES);
//		this.dimTipo = (Spinner) rootView.findViewById(R.id.tipoDim);
//		this.dimTipo.setAdapter(new ArrayAdapter<SelectionObject>(this.getActivity(),android.R.layout.simple_list_item_1,SelectionObject.getDimTipoData()));
		this.productos.setAdapter(new PedidoLineaAdapter(this.getActivity()));
		this.provee = (TextView) rootView.findViewById(R.id.proveedorNombre);
		this.pedidos = (Spinner) rootView.findViewById(R.id.pedidosCombo);
		this.pedidos.setOnItemSelectedListener(this);

		this.pedDao = new PedidoDAO(this);
		this.pedDao.getPedidosPend(AntonConstants.RAW_PICKING);

//		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
//				this.productos,
//				new SwipeDismissListViewTouchListener.DismissCallbacks() {
//					@Override
//					public boolean canDismiss(int position) {
//						return true;
//					}
//
//					@Override
//					public void onDismiss(ListView listView,
//							int[] reverseSortedPositions) {
//						for (int position : reverseSortedPositions) {
//							((PedidoLineaAdapter) productos.getAdapter()).delLinea((PedidoLinea) productos.getAdapter().getItem(position));
//						}
//						((PedidoLineaAdapter) productos.getAdapter()).notifyDataSetChanged();
//					}
//				});
//		this.productos.setOnTouchListener(touchListener);
//		this.productos.setOnScrollListener(touchListener.makeScrollListener());

		return rootView;
	}


	public void popSearchMP(View view) {
		SearchMPPopupFragment popconf = new SearchMPPopupFragment();
		popconf.setTargetFragment(this, 1234);
		popconf.show(getFragmentManager(),"Server_Search");
	}

	@Override
	public void setMateriaPrima() {
		this.productos.setAdapter(new ArrayAdapter<MateriaPrima>(this.getActivity(),android.R.layout.simple_list_item_1,this.mpDao.getMateriaPrimasList()));	
	}


	public void confirmStock() {
			PedidoLinea[] pls = new PedidoLinea[this.productos.getAdapter().getCount()];
			for (int i = 0; i < this.productos.getAdapter().getCount(); i++) {
				pls[i] =  (PedidoLinea) this.productos.getAdapter().getItem(i);
			}
			ConfirmMovesAsyncTask confAction = new ConfirmMovesAsyncTask(getActivity());
			confAction.setMoveType("in");
			confAction.setModelStockPicking("stock.move");
			confAction.execute(pls);
	}

	@Override
	public void setPedidos() {
		this.pedidos.setAdapter(new ArrayAdapter<Pedido>(this.getActivity(),android.R.layout.simple_list_item_1,this.pedDao.getPedidoList()));
	}

	@Override
	public void searchByEAN(String scanContent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,long arg3) {
		Pedido ped = (Pedido) arg0.getItemAtPosition(pos);
		this.provee.setText((String) ped.getPartner()[1]);
		this.pedDao = new PedidoDAO(this);
		this.pedDao.getMoveByPed(ped.getId());
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPedidosLineas() {
		PedidoLineaAdapter pla =  new PedidoLineaAdapter(this,this.pedDao.getPedidoLineaList());
		pla.setForCheck(true);
		this.productos.setAdapter(pla);
	}
}